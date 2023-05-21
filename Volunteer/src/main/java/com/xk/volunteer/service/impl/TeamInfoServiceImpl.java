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
import com.xk.volunteer.service.TeamInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Administrator
* @description 针对表【t_team_info】的数据库操作Service实现
* @createDate 2022-07-22 16:26:10
*/
@Service
@Transactional
public class TeamInfoServiceImpl extends ServiceImpl<TeamInfoMapper, TeamInfo>
    implements TeamInfoService{
    private static final String REDIS_VERIFY_CODE_PREFIX = "USER_PHONE:";

    @Autowired
    private TeamInfoMapper teamInfoMapper;
    @Autowired
    private ApplyInfoService applyInfoService;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private MessageInfoService messageInfoService;
    @Autowired
    private DonateInfoMapper donateInfoMapper;
    @Autowired
    private MessageInfoMapper messageInfoMapper;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Long getTeamCount() {
        return teamInfoMapper.selectCount(new QueryWrapper<TeamInfo>().eq("TEAM_STATUS",1));
    }

    @Override
    public List<TeamInfo> getMostFiveOrderByHours() {
        return teamInfoMapper.selectTeamIdAndTeamNameAndTeamHoursOrderByTeamHoursDesc();
    }

    @Override
    public Integer register(RegisterUser registerUser) {
        TeamInfo teamInfo = new TeamInfo();
        teamInfo.setTeamPhone(registerUser.getOrganizationPhone());
        teamInfo.setTeamEmail(registerUser.getOrganizationEmail());
        teamInfo.setTeamPassword(registerUser.getPassword());
        teamInfo.setTeamHeadName(registerUser.getHeadName());
        teamInfo.setTeamHeadEmail(registerUser.getPersonalEmail());
        teamInfo.setTeamHeadPhone(registerUser.getPersonalPhone());
        teamInfo.setTeamHeadIdentity(registerUser.getHeadCertificateNo());
        teamInfo.setTeamHeadPicture(registerUser.getHeadCertificatePicture());
        teamInfo.setTeamName(registerUser.getUsername());
        teamInfo.setTeamClassic(registerUser.getOrganizationType());
        teamInfo.setTeamAddress(registerUser.getAddress());
        teamInfo.setTeamObject(registerUser.getServiceObject());
        teamInfo.setTeamProve(registerUser.getProvePicture());
        teamInfo.setTeamSimple(registerUser.getIntroduction());
        teamInfo.setTeamAvatar("https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/defaultAvatar1.jpg");
        teamInfo.setTeamRegister(DateUtil.today());
        int insert = teamInfoMapper.insert(teamInfo);
        if (insert > 0) {
            //添加对应申请信息，等待管理员审核
            ApplyInfo applyInfo = new ApplyInfo();
            applyInfo.setApplyTitle("志愿者团队注册");
            applyInfo.setApplyDescription("志愿者团队注册");
            //设置申请类型为志愿者团队注册
            applyInfo.setApplyType(3);
            //设置申请人为新注册的志愿者团队
            applyInfo.setFkApplicantId(teamInfo.getTeamId());
            applyInfo.setFkApplicantType(1);
            //设置审核人为管理员
            applyInfo.setFkAuditorId(1);
            applyInfo.setFkAuditorType(3);
            //调用发起消息通知的方法
            messageInfoService.insertMessage("志愿者团队注册审核通知","",teamInfo.getTeamId(),1,1,3,null);
            return applyInfoService.insertApply(applyInfo);
        }
        return -1;
    }

    @Override
    public String login(LoginUser loginUser) {
        TeamInfo teamInfo = null;
        //根据登陆方式进行判断
        //邮箱登录
        if (loginUser.getLoginType() == 0) {
            teamInfo = teamInfoMapper.selectOneByTeamEmailAndTeamPasswordAndTeamStatus(loginUser.getEmail(), loginUser.getPassword(), 1);
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
            teamInfo = teamInfoMapper.selectOneByTeamHeadPhoneAndTeamStatus(loginUser.getPhone(),1);
        }
        //根据查询结果判断是否登陆成功
        if (teamInfo != null) {
            return JWT.create()
                    .setPayload("userId",teamInfo.getTeamId())
                    .setPayload("userNickName",teamInfo.getTeamName())
                    .setPayload("userAvatar",teamInfo.getTeamAvatar())
                    .setPayload("userRegisterTime",teamInfo.getTeamRegister())
                    .setPayload("userType",teamInfo.getTeamType())
                    .setKey(teamInfo.getTeamPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public Map<Object, Object> queryTeamProfile(Integer userId) {
        TeamInfo teamInfo = teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().eq("TEAM_ID",userId));
        if (teamInfo != null) {
            teamInfo.setTeamHeadIdentity(teamInfo.getTeamHeadIdentity().substring(0,6) + "**********" + teamInfo.getTeamHeadIdentity().substring(16));
            HashMap<Object, Object> map = new HashMap<>(20);
            map.put("userId",teamInfo.getTeamId());
            map.put("userNickName",teamInfo.getTeamName());
            map.put("userEmail",teamInfo.getTeamEmail());
            map.put("userPhone",teamInfo.getTeamPhone());
            map.put("userAvatar",teamInfo.getTeamAvatar());
            map.put("userAddress",teamInfo.getTeamAddress());
            map.put("userClassic",teamInfo.getTeamClassic());
            map.put("userServiceObj",teamInfo.getTeamObject());
            map.put("userProve",teamInfo.getTeamProve());
            map.put("userRegisterTime",teamInfo.getTeamRegister());
            map.put("userIntroduction",teamInfo.getTeamSimple());
            map.put("userHeadName",teamInfo.getTeamHeadName());
            map.put("userHeadEmail",teamInfo.getTeamHeadEmail());
            map.put("userHeadPhone",teamInfo.getTeamHeadPhone());
            map.put("userHeadCertificate",teamInfo.getTeamHeadIdentity());
            map.put("userHours",teamInfo.getTeamHours());
            map.put("userActiveCount",teamInfo.getTeamActivecount());
            map.put("userCount",teamInfo.getTeamCount());
            map.put("userType",teamInfo.getTeamType());
            return map;
        }
        return null;
    }

    @Override
    public String getTeamNickName(Integer userId) {
        return teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().select("TEAM_NAME").eq("TEAM_ID",userId)).getTeamName();
    }

    @Override
    public Page<TeamInfo> getTeamByCondition(String province, String city, String classic, String serviceObj, Page<TeamInfo> page) {
        QueryWrapper<TeamInfo> wrapper = new QueryWrapper<>();
        wrapper.select("TEAM_ID,TEAM_AVATAR,TEAM_NAME,TEAM_COUNT,TEAM_HOURS,TEAM_ADDRESS");
        wrapper.eq("TEAM_STATUS",1);
        //当查询条件中省不为空
        if (StrUtil.isNotEmpty(province)) {
            StringBuffer area = new StringBuffer(province);
            //当查询条件中市不为空
            if (StrUtil.isNotEmpty(city))
                area.append(city);
            wrapper.likeRight("TEAM_ADDRESS",area);
        }
        //当查询条件中分类不为空
        if (StrUtil.isNotEmpty(classic)) {
            wrapper.eq("TEAM_CLASSIC",classic);
        }
        if (StrUtil.isNotEmpty(serviceObj)) {
            wrapper.like("TEAM_OBJECT",serviceObj);
        }
        return teamInfoMapper.selectPage(page,wrapper);
    }

    @Override
    public String updateInfo(UpdateUser updateUser) {
        TeamInfo teamInfo = new TeamInfo();
        teamInfo.setTeamId(updateUser.getUserId());
        teamInfo.setTeamAvatar(updateUser.getAvatar());
        teamInfo.setTeamName(updateUser.getUsername());
        teamInfo.setTeamEmail(updateUser.getEmail());
        teamInfo.setTeamPhone(updateUser.getPhone());
        teamInfo.setTeamAddress(updateUser.getAddress());
        teamInfo.setTeamClassic(updateUser.getOrganizationType());
        teamInfo.setTeamObject(updateUser.getServiceObject());
        teamInfo.setTeamSimple(updateUser.getIntroduction());
        if (teamInfoMapper.updateById(teamInfo) > 0) {
            TeamInfo newTeam = teamInfoMapper.selectById(teamInfo.getTeamId());
            return JWT.create()
                    .setPayload("userId",newTeam.getTeamId())
                    .setPayload("userNickName",newTeam.getTeamName())
                    .setPayload("userAvatar",newTeam.getTeamAvatar())
                    .setPayload("userRegisterTime",newTeam.getTeamRegister())
                    .setPayload("userType",newTeam.getTeamType())
                    .setKey(newTeam.getTeamPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public boolean updateHead(UpdateHeader updateHeader) {
        TeamInfo teamInfo = new TeamInfo();
        teamInfo.setTeamId(updateHeader.getUserId());
        teamInfo.setTeamHeadEmail(updateHeader.getPersonalEmail());
        teamInfo.setTeamHeadPhone(updateHeader.getPersonalPhone());
        if (StrUtil.equals(updateHeader.getUpdateHeadType(),"replace")) {
            teamInfo.setTeamHeadName(updateHeader.getName());
            teamInfo.setTeamHeadIdentity(updateHeader.getCertificateNo());
            teamInfo.setTeamHeadPicture(updateHeader.getCertificatePicture());
        }
        return teamInfoMapper.updateById(teamInfo) > 0;
    }

    @Override
    public String modifyPassword(ModifyPassword modifyPassword) {
        //如果邮箱不为空，说明是忘记密码情况下的修改
        if (StrUtil.isNotBlank(modifyPassword.getEmail())) {
            //判断输入的邮箱是否存在
            //如果有该用户
            if (teamInfoMapper.exists(new QueryWrapper<TeamInfo>().eq("TEAM_EMAIL",modifyPassword.getEmail()))) {
                TeamInfo teamInfo = teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().eq("TEAM_EMAIL", modifyPassword.getEmail()));
                teamInfo.setTeamPassword(modifyPassword.getNewPassword());
                int update = teamInfoMapper.updateById(teamInfo);
                if (update > 0) return "success";
                return "error";
            }
            return "不存在该用户";
        }
        //邮箱为空，说明是修改密码
        //根据修改密码的用户id查询
        TeamInfo teamInfo = teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().eq("TEAM_ID", modifyPassword.getUserId()));
        //判断输入的旧密码是否相等
        //如果不相等
        if (!StrUtil.equals(teamInfo.getTeamPassword(),modifyPassword.getOldPassword())) {
            return "旧密码错误";
        }
        //相等
        teamInfo.setTeamPassword(modifyPassword.getNewPassword());
        int update = teamInfoMapper.updateById(teamInfo);
        if (update > 0) return "success";
        return "error";
    }

    @Override
    public Map<Object, Object> getTeamInfo(Integer userId,Integer userType,Integer currentPage) {
        Integer teamId = userId;
        if (userType == 0) {
            //如何是查询志愿者所在团队的信息，则先查询志愿者所在团队的id，再查询团队信息
            teamId = volunteersInfoMapper.selectById(userId).getFkVolunteersTeam();
            if (teamId == null) return null;
        }
        //查询团队的成员
        Page<VolunteersInfo> page = new Page<>(currentPage,15);
        QueryWrapper<VolunteersInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("VOLUNTEERS_STATUS",1);
        wrapper.eq("FK_VOLUNTEERS_TEAM",teamId);
        Page<VolunteersInfo> volunteersInfoPage = volunteersInfoMapper.selectPage(page, wrapper);
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("memberList",volunteersInfoPage.getRecords());
        map.put("teamInfo",teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().eq("TEAM_ID",teamId)));
        return map;
    }

    @Override
    public boolean memberKickOrExit(List<Integer> volunteerIds, Integer teamId) {
        TeamInfo teamInfo = teamInfoMapper.selectById(teamId);
        Integer teamHours = teamInfo.getTeamHours();
        Integer teamCount = teamInfo.getTeamCount();
        Integer teamActivecount = teamInfo.getTeamActivecount();
        for (Integer volunteerId : volunteerIds) {
            if (volunteersInfoMapper.update(null,new UpdateWrapper<VolunteersInfo>()
                    .eq("VOLUNTEERS_ID",volunteerId)
                    .set("FK_VOLUNTEERS_TEAM",null)) > 0) {
                VolunteersInfo volunteersInfo = volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>()
                        .eq("VOLUNTEERS_ID",volunteerId));
                teamHours = teamHours - volunteersInfo.getVolunteersHours();
                teamActivecount = teamActivecount - volunteersInfo.getVolunteersActivecount();
                teamCount = teamCount - 1;
            }
        }
        return teamInfoMapper.update(null, new UpdateWrapper<TeamInfo>()
                .eq("TEAM_ID", teamId)
                .set("TEAM_HOURS", teamHours)
                .set("TEAM_ACTIVECOUNT", teamActivecount)
                .set("TEAM_COUNT", teamCount)) > 0;
    }

    @Override
    public boolean changeStatus(Integer userId, Integer status) {
        UpdateWrapper<TeamInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("TEAM_ID",userId)
                .set("TEAM_STATUS",status);
        return teamInfoMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean deleteAccount(List<Integer> userIds) {
        if (userIds.size() > 0) {
            userIds.forEach(userId -> {
                TeamInfo teamInfo = teamInfoMapper.selectById(userId);
                //先将本团队的所有成员踢出
                volunteersInfoMapper.update(null,new UpdateWrapper<VolunteersInfo>()
                        .eq("FK_VOLUNTEERS_TEAM",userId)
                        .set("FK_VOLUNTEERS_TEAM",null));
                //删除消息记录
                messageInfoMapper.delete(new QueryWrapper<MessageInfo>()
                        .eq("FK_MESSAGE_SENDER",userId)
                        .eq("FK_MESSAGE_SENDERTYPE",1)
                        .or(i -> i.eq("FK_MESSAGE_ACCEPTER",userId)
                                .eq("FK_MESSAGE_ACCEPTERTYPE",1)));
                //删除捐赠记录
                donateInfoMapper.delete(new QueryWrapper<DonateInfo>()
                        .eq("FK_DONATE_USER",userId)
                        .eq("FK_DONATE_USERTYPE",1));
                //删除申请记录
                applyInfoMapper.delete(new QueryWrapper<ApplyInfo>()
                        .eq("FK_APPLICANT_ID",userId)
                        .eq("FK_APPLICANT_TYPE",1)
                        .or(i -> i.eq("FK_AUDITOR_ID",userId)
                                .eq("FK_AUDITOR_TYPE",1)));
                teamInfoMapper.deleteById(userId);
            });
            return true;
        }
        return false;
    }
}




