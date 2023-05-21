package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.controller.pojo.*;
import com.xk.volunteer.entity.*;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.ApplyInfoService;
import com.xk.volunteer.service.MessageInfoService;
import com.xk.volunteer.service.UnitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
* @author Administrator
* @description 针对表【t_unit_info】的数据库操作Service实现
* @createDate 2022-07-22 16:26:10
*/
@Service
@Transactional
public class UnitInfoServiceImpl extends ServiceImpl<UnitInfoMapper, UnitInfo>
    implements UnitInfoService{
    private static final String REDIS_VERIFY_CODE_PREFIX = "USER_PHONE:";

    @Autowired
    private UnitInfoMapper unitInfoMapper;
    @Autowired
    private ApplyInfoService applyInfoService;
    @Autowired
    private MessageInfoService messageInfoService;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private DonateInfoMapper donateInfoMapper;
    @Autowired
    private MessageInfoMapper messageInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Long getUnitCount() {
        return unitInfoMapper.selectCount(new QueryWrapper<UnitInfo>().eq("UNIT_STATUS",1));
    }

    @Override
    public Integer register(RegisterUser registerUser) {
        UnitInfo unitInfo = new UnitInfo();
        unitInfo.setUnitEmail(registerUser.getOrganizationEmail());
        unitInfo.setUnitPhone(registerUser.getOrganizationPhone());
        unitInfo.setUnitPassword(registerUser.getPassword());
        unitInfo.setUnitHeadName(registerUser.getHeadName());
        unitInfo.setUnitHeadEmail(registerUser.getPersonalEmail());
        unitInfo.setUnitHeadPhone(registerUser.getPersonalPhone());
        unitInfo.setUnitHeadIdentity(registerUser.getHeadCertificateNo());
        unitInfo.setUnitHeadPicture(registerUser.getHeadCertificatePicture());
        unitInfo.setUnitName(registerUser.getUsername());
        unitInfo.setUnitClassic(registerUser.getOrganizationType());
        unitInfo.setUnitAddress(registerUser.getAddress());
        unitInfo.setUnitProve(registerUser.getProvePicture());
        unitInfo.setUnitSimple(registerUser.getIntroduction());
        unitInfo.setUnitAvatar("https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/defaultAvatar1.jpg");
        unitInfo.setUnitRegister(DateUtil.today());
        int insert = unitInfoMapper.insert(unitInfo);
        if (insert > 0) {
            //添加对应申请信息，等待管理员审核
            ApplyInfo applyInfo = new ApplyInfo();
            applyInfo.setApplyTitle("志愿单位注册");
            applyInfo.setApplyDescription("志愿单位注册");
            //设置申请类型为志愿单位注册
            applyInfo.setApplyType(4);
            //设置申请人为新注册的志愿单位
            applyInfo.setFkApplicantId(unitInfo.getUnitId());
            applyInfo.setFkApplicantType(2);
            //设置审核人为管理员
            applyInfo.setFkAuditorId(1);
            applyInfo.setFkAuditorType(3);
            //调用发起消息通知的方法
            messageInfoService.insertMessage("志愿单位注册审核通知","",unitInfo.getUnitId(),2,1,3,null);
            return applyInfoService.insertApply(applyInfo);
        }
        return -1;
    }

    @Override
    public String login(LoginUser loginUser) {
        UnitInfo unitInfo = null;
        //根据登陆方式进行判断
        //邮箱登录
        if (loginUser.getLoginType() == 0) {
            unitInfo = unitInfoMapper.selectOneByUnitEmailAndUnitPasswordAndUnitStatus(loginUser.getEmail(), loginUser.getPassword(), 1);
        } else if (loginUser.getLoginType() == 1) {
            //手机号登陆
            //判断验证码是否正确
            String verifyCode = redisTemplate.opsForValue().get(REDIS_VERIFY_CODE_PREFIX + loginUser.getPhone());
            if (StrUtil.isEmpty(verifyCode)) {
                return "验证码不存在或已过期";
            }
            if (!loginUser.getVerifyCode().equals(verifyCode)) {
                return "验证码不正确";
            }
            //验证码相等，删除验证码
            redisTemplate.delete(REDIS_VERIFY_CODE_PREFIX + loginUser.getPhone());
            unitInfo = unitInfoMapper.selectOneByUnitHeadPhoneAndUnitStatus(loginUser.getPhone(),1);
        }
        //根据查询结果判断是否登陆成功
        if (unitInfo != null) {
            return JWT.create()
                    .setPayload("userId",unitInfo.getUnitId())
                    .setPayload("userNickName",unitInfo.getUnitName())
                    .setPayload("userAvatar",unitInfo.getUnitAvatar())
                    .setPayload("userRegisterTime",unitInfo.getUnitRegister())
                    .setPayload("userType",unitInfo.getUnitType())
                    .setKey(unitInfo.getUnitPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public Map<Object, Object> queryUnitProfile(Integer userId) {
        UnitInfo unitInfo = unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().eq("UNIT_ID",userId));
        if (unitInfo != null) {
            unitInfo.setUnitHeadIdentity(unitInfo.getUnitHeadIdentity().substring(0,6) + "**********" + unitInfo.getUnitHeadIdentity().substring(16));
            HashMap<Object, Object> map = new HashMap<>(20);
            map.put("userId",unitInfo.getUnitId());
            map.put("userNickName",unitInfo.getUnitName());
            map.put("userEmail",unitInfo.getUnitEmail());
            map.put("userPhone",unitInfo.getUnitPhone());
            map.put("userAvatar",unitInfo.getUnitAvatar());
            map.put("userAddress",unitInfo.getUnitAddress());
            map.put("userClassic",unitInfo.getUnitClassic());
            map.put("userProve",unitInfo.getUnitProve());
            map.put("userRegisterTime",unitInfo.getUnitRegister());
            map.put("userIntroduction",unitInfo.getUnitSimple());
            map.put("userHeadName",unitInfo.getUnitHeadName());
            map.put("userHeadEmail",unitInfo.getUnitHeadEmail());
            map.put("userHeadPhone",unitInfo.getUnitHeadPhone());
            map.put("userHeadCertificate",unitInfo.getUnitHeadIdentity());
            map.put("userActiveCount",unitInfo.getUnitPubliccount());
            map.put("userType",unitInfo.getUnitType());
            return map;
        }
        return null;
    }

    @Override
    public String getUnitNickName(Integer userId) {
        return unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().select("UNIT_NAME").eq("UNIT_ID",userId)).getUnitName();
    }

    @Override
    public Page<UnitInfo> getUnitByCondition(String province, String city, String classic, Page<UnitInfo> page) {
        QueryWrapper<UnitInfo> wrapper = new QueryWrapper<>();
        wrapper.select("UNIT_ID,UNIT_AVATAR,UNIT_NAME,UNIT_PUBLICCOUNT,UNIT_ADDRESS");
        wrapper.eq("UNIT_STATUS",1);
        //当查询条件中省不为空
        if (StrUtil.isNotEmpty(province)) {
            StringBuffer area = new StringBuffer(province);
            //当查询条件中市不为空
            if (StrUtil.isNotEmpty(city))
                area.append(city);
            wrapper.likeRight("UNIT_ADDRESS",area);
        }
        //当查询条件中分类不为空
        if (StrUtil.isNotEmpty(classic)) {
            wrapper.eq("UNIT_CLASSIC",classic);
        }
        return unitInfoMapper.selectPage(page,wrapper);
    }

    @Override
    public String updateInfo(UpdateUser updateUser) {
        UnitInfo unitInfo = new UnitInfo();
        unitInfo.setUnitId(updateUser.getUserId());
        unitInfo.setUnitAvatar(updateUser.getAvatar());
        unitInfo.setUnitName(updateUser.getUsername());
        unitInfo.setUnitEmail(updateUser.getEmail());
        unitInfo.setUnitPhone(updateUser.getPhone());
        unitInfo.setUnitAddress(updateUser.getAddress());
        unitInfo.setUnitClassic(updateUser.getOrganizationType());
        unitInfo.setUnitSimple(updateUser.getIntroduction());
        if (unitInfoMapper.updateById(unitInfo) > 0) {
            UnitInfo newUnit = unitInfoMapper.selectById(unitInfo.getUnitId());
            return JWT.create()
                    .setPayload("userId",newUnit.getUnitId())
                    .setPayload("userNickName",newUnit.getUnitName())
                    .setPayload("userAvatar",newUnit.getUnitAvatar())
                    .setPayload("userRegisterTime",newUnit.getUnitRegister())
                    .setPayload("userType",newUnit.getUnitType())
                    .setKey(newUnit.getUnitPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public boolean updateHead(UpdateHeader updateHeader) {
        UnitInfo unitInfo = new UnitInfo();
        unitInfo.setUnitId(updateHeader.getUserId());
        unitInfo.setUnitHeadEmail(updateHeader.getPersonalEmail());
        unitInfo.setUnitHeadPhone(updateHeader.getPersonalPhone());
        if (StrUtil.equals(updateHeader.getUpdateHeadType(),"replace")) {
            unitInfo.setUnitHeadName(updateHeader.getName());
            unitInfo.setUnitHeadIdentity(updateHeader.getCertificateNo());
            unitInfo.setUnitHeadPicture(updateHeader.getCertificatePicture());
        }
        return unitInfoMapper.updateById(unitInfo) > 0;
    }

    @Override
    public String modifyPassword(ModifyPassword modifyPassword) {
        //如果邮箱不为空，说明是忘记密码情况下的修改
        if (StrUtil.isNotBlank(modifyPassword.getEmail())) {
            //判断输入的邮箱是否存在
            //如果有该用户
            if (unitInfoMapper.exists(new QueryWrapper<UnitInfo>().eq("UNIT_EMAIL",modifyPassword.getEmail()))) {
                UnitInfo unitInfo = unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().eq("UNIT_EMAIL", modifyPassword.getEmail()));
                unitInfo.setUnitPassword(modifyPassword.getNewPassword());
                int update = unitInfoMapper.updateById(unitInfo);
                if (update > 0) return "success";
                return "error";
            }
            return "不存在该用户";
        }
        //邮箱为空，说明是修改密码
        //根据修改密码的用户id查询
        UnitInfo unitInfo = unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().eq("UNIT_ID", modifyPassword.getUserId()));
        //判断输入的旧密码是否相等
        //如果不相等
        if (!StrUtil.equals(unitInfo.getUnitPassword(),modifyPassword.getOldPassword())) {
            return "旧密码错误";
        }
        //相等
        unitInfo.setUnitPassword(modifyPassword.getNewPassword());
        int update = unitInfoMapper.updateById(unitInfo);
        if (update > 0) return "success";
        return "error";
    }

    @Override
    public boolean changeStatus(Integer userId, Integer status) {
        UpdateWrapper<UnitInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("UNIT_ID",userId)
                .set("UNIT_STATUS",status);
        return unitInfoMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean deleteAccount(List<Integer> userIds) {
        if (userIds.size() > 0) {
            userIds.forEach(userId -> {
                //删除发布的活动记录
                activityInfoMapper.delete(new QueryWrapper<ActivityInfo>()
                        .eq("FK_ACTIVITY_PUBLISHER",userId));
                //删除消息记录
                messageInfoMapper.delete(new QueryWrapper<MessageInfo>()
                        .eq("FK_MESSAGE_SENDER",userId)
                        .eq("FK_MESSAGE_SENDERTYPE",2)
                        .or(i -> i.eq("FK_MESSAGE_ACCEPTER",userId)
                                .eq("FK_MESSAGE_ACCEPTERTYPE",2)));
                //删除捐赠记录
                donateInfoMapper.delete(new QueryWrapper<DonateInfo>()
                        .eq("FK_DONATE_USER",userId)
                        .eq("FK_DONATE_USERTYPE",2));
                //删除申请记录
                applyInfoMapper.delete(new QueryWrapper<ApplyInfo>()
                        .eq("FK_APPLICANT_ID",userId)
                        .eq("FK_APPLICANT_TYPE",1)
                        .or(i -> i.eq("FK_AUDITOR_ID",userId)
                                .eq("FK_AUDITOR_TYPE",1)));
                unitInfoMapper.deleteById(userId);
            });
            return true;
        }
        return false;
    }
}




