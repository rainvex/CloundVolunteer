package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.controller.pojo.LoginUser;
import com.xk.volunteer.controller.pojo.ModifyPassword;
import com.xk.volunteer.controller.pojo.UpdateUser;
import com.xk.volunteer.entity.*;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Administrator
* @description 针对表【t_admin_info(管理员信息表)】的数据库操作Service实现
* @createDate 2022-07-22 16:26:09
*/
@Service
@Transactional
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfo>
    implements AdminInfoService{
    private static final String REDIS_VERIFY_CODE_PREFIX = "USER_PHONE:";

    @Autowired
    private AdminInfoMapper adminInfoMapper;
    @Autowired
    private VolunteersInfoService volunteersInfoService;
    @Autowired
    private TeamInfoService teamInfoService;
    @Autowired
    private UnitInfoService unitInfoService;
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private TeamInfoMapper teamInfoMapper;
    @Autowired
    private UnitInfoMapper unitInfoMapper;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public String login(LoginUser loginUser) {
        AdminInfo adminInfo = null;
        //根据登陆方式进行判断
        //邮箱登录
        if (loginUser.getLoginType() == 0) {
            adminInfo = adminInfoMapper.selectOneByAdminEmailAndAdminPassword(loginUser.getEmail(), loginUser.getPassword());
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
            adminInfo = adminInfoMapper.selectOneByAdminPhone(loginUser.getPhone());
        }
        //根据查询结果判断是否登陆成功
        if (adminInfo != null) {
            return JWT.create()
                    .setPayload("userId",adminInfo.getAdminId())
                    .setPayload("userAvatar",adminInfo.getAdminAvatar())
                    .setPayload("userNickName",adminInfo.getAdminName())
                    .setPayload("userEmail",adminInfo.getAdminEmail())
                    .setPayload("userPhone",adminInfo.getAdminPhone())
                    .setPayload("userType",adminInfo.getAdminType())
                    .setKey(adminInfo.getAdminPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public Map<Object, Object> queryAdminProfile(Integer userId) {
        AdminInfo adminInfo = adminInfoMapper.selectOne(new QueryWrapper<AdminInfo>().eq("ADMIN_ID",userId));
        if (adminInfo != null) {
            ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>(10);
            map.put("userId",adminInfo.getAdminId());
            map.put("userAvatar",adminInfo.getAdminAvatar());
            map.put("userNickName",adminInfo.getAdminName());
            map.put("userEmail",adminInfo.getAdminEmail());
            map.put("userPhone",adminInfo.getAdminPhone());
            map.put("userType",adminInfo.getAdminType());
            return map;
        }
        return null;
    }

    @Override
    public String getAdminNickName(Integer userId) {
        return adminInfoMapper.selectOne(new QueryWrapper<AdminInfo>().select("ADMIN_NAME").eq("ADMIN_ID",userId)).getAdminName();
    }

    @Override
    public String updateInfo(UpdateUser updateUser) {
        return "";
    }

    @Override
    public String modifyPassword(ModifyPassword modifyPassword) {
        return null;
    }

    @Override
    public Map<Object, Object> home() {
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        //查询已注册志愿者用户数量
        map.put("volunteerCount",volunteersInfoService.getVolunteerCount());
        //查询已注册志愿者团队用户数量
        map.put("teamCount",teamInfoService.getTeamCount());
        //查询已注册志愿单位用户数量
        map.put("unitCount",unitInfoService.getUnitCount());
        //查询已发布志愿活动数量
        map.put("activityCount",activityInfoService.getActivityCount());
        //查询系统用户参与志愿活动总时长
        map.put("volunteerHours",volunteersInfoService.getTotalHours());
        //查询志愿活动按分类分组后的数据条数
        map.put("activityClassicCountList",activityInfoMapper.selectActivityClassicCount());
        //查询每个省的注册志愿者数量
        map.put("provinceVolunteerCountList",volunteersInfoMapper.selectProvinceVolunteerCount());
        return map;
    }

    @Override
    public List getUserList(Integer userType, Integer currentPage) {
        if (userType == 0) {
            QueryWrapper<VolunteersInfo> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("VOLUNTEERS_REGISTER");
            Page<VolunteersInfo> volunteersInfoPage = volunteersInfoMapper.selectPage(new Page<VolunteersInfo>(currentPage, 15), wrapper);
            List<VolunteersInfo> volunteersInfoList = volunteersInfoPage.getRecords();
            if (volunteersInfoList.size() > 0) {
                volunteersInfoList.forEach(volunteersInfo -> {
                    if (volunteersInfo.getFkVolunteersTeam() != null) {
                        TeamInfo teamInfo = teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().eq("TEAM_ID", volunteersInfo.getFkVolunteersTeam()));
                        volunteersInfo.setTeamInfo(teamInfo);
                    }
                });
            }
            return volunteersInfoList;
        } else if (userType == 1) {
            QueryWrapper<TeamInfo> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("TEAM_REGISTER");
            Page<TeamInfo> teamInfoPage = teamInfoMapper.selectPage(new Page<TeamInfo>(currentPage, 15), wrapper);
            return teamInfoPage.getRecords();
        } else if (userType == 2) {
            QueryWrapper<UnitInfo> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("UNIT_REGISTER");
            Page<UnitInfo> unitInfoPage = unitInfoMapper.selectPage(new Page<UnitInfo>(currentPage, 15), wrapper);
            return unitInfoPage.getRecords();
        }
        return null;
    }
}




