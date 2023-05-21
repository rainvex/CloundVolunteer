package com.xk.volunteer.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.xk.volunteer.controller.pojo.*;
import com.xk.volunteer.entity.*;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.*;
import com.xk.volunteer.utils.SMSUtil;
import com.xk.volunteer.utils.TxCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author Rainvex
 * @Date 2022/7/28
 * @Name BaseServiceImpl
 */
@Service
@Transactional
public class BaseServiceImpl implements BaseService {
    private static final String REDIS_VERIFY_CODE_PREFIX = "USER_PHONE:";
    @Autowired
    private VolunteersInfoService volunteersInfoService;
    @Autowired
    private TeamInfoService teamInfoService;
    @Autowired
    private UnitInfoService unitInfoService;
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private TeamInfoMapper teamInfoMapper;
    @Autowired
    private UnitInfoMapper unitInfoMapper;
    @Autowired
    private AdminInfoMapper adminInfoMapper;
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private InformInfoService informInfoService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public String getPasswordByToken(Integer userId, Integer userType) {
        String password = "";
        //根据登录用户类型调用不同service
        if (userType == 0) {
            VolunteersInfo volunteersInfo = volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>().eq("VOLUNTEERS_ID",userId));
            if (volunteersInfo != null) password = volunteersInfo.getVolunteersPassword();
        } else if (userType == 1) {
            TeamInfo teamInfo = teamInfoMapper.selectOne(new QueryWrapper<TeamInfo>().eq("TEAM_ID",userId));
            if (teamInfo != null) password = teamInfo.getTeamPassword();
        } else if (userType == 2) {
            UnitInfo unitInfo = unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().eq("UNIT_ID",userId));
            if (unitInfo != null) password = unitInfo.getUnitPassword();
        } else {
            AdminInfo adminInfo = adminInfoMapper.selectOne(new QueryWrapper<AdminInfo>().eq("ADMIN_ID",userId));
            if (adminInfo != null) password = adminInfo.getAdminPassword();
        }
        return password;
    }

    @Override
    public Map<String, Object> index() {
        //查询已注册的志愿者数量
        Long volunteerCount = volunteersInfoService.getVolunteerCount();
        //查询所有志愿者的志愿时长总和
        Long totalHours = volunteersInfoService.getTotalHours();
        //获取志愿时长最多的五位志愿者
        List<VolunteersInfo> mostFiveVolunteerByHours = volunteersInfoService.getMostFiveOrderByHours();
        //查询已注册的志愿者团队数量
        Long teamCount = teamInfoService.getTeamCount();
        //获取志愿时长总和最多的志愿者团队
        List<TeamInfo> mostFiveTeamByHours = teamInfoService.getMostFiveOrderByHours();
        //查询已注册的志愿单位数量
        Long unitCount = unitInfoService.getUnitCount();
        //查询发布时间最新且参与活动申请数量最多的志愿活动
        List<ActivityInfo> activityInfoList = activityInfoService.getActivityMostApplyAndPublic();
        //查询志愿时长最长且收到入团申请最多的志愿者团队
        List<TeamInfo> teamInfoList = teamInfoMapper.selectMostApplyAndHourLimit();
        //查询发布志愿活动数量最多且收到参加活动申请最多的志愿单位
        List<UnitInfo> unitInfoList = unitInfoMapper.selectMostApplyAndCountLimit();
        //查询热点最高的前五条志愿资讯
        List<InformInfo> informInfoList = informInfoService.selectInformHighView();
        ConcurrentHashMap<String, Object> indexData = new ConcurrentHashMap<>();
        indexData.put("volunteerCount",volunteerCount);
        indexData.put("totalHours",totalHours);
        indexData.put("mostFiveVolunteerByHours",mostFiveVolunteerByHours);
        indexData.put("teamCount",teamCount);
        indexData.put("mostFiveTeamByHours",mostFiveTeamByHours);
        indexData.put("unitCount",unitCount);
        indexData.put("teamInfoList",teamInfoList);
        indexData.put("unitInfoList",unitInfoList);
        indexData.put("activityInfoList",activityInfoList);
        indexData.put("informList",informInfoList);
        return indexData;
    }

    @Override
    public String getVerifyCode(String phone, Integer userType, String verifyLogo) {
        //根据标识判断获取验证码的用处
        //如果是标识是登录
        if (verifyLogo.equals("login")) {
            //查看该用户是否存在且账号是否是启用状态
            Object o = null;
            if (userType == 0) o = volunteersInfoMapper.selectOneByVolunteersPhoneAndVolunteersStatus(phone, 1);
            else if (userType == 1) o = teamInfoMapper.selectOneByTeamHeadPhoneAndTeamStatus(phone, 1);
            else if (userType == 2) o = unitInfoMapper.selectOneByUnitHeadPhoneAndUnitStatus(phone, 1);
            else o = adminInfoMapper.selectOneByAdminPhone(phone);
            //如果用户不存在则返回
            if (o == null) return "该用户不存在";
        }
        //如果标识是注册，则直接查看是否已获取过验证码
        //查看redis中是否已经有该手机号对应的验证码
        String existedSmsCode = redisTemplate.opsForValue().get(REDIS_VERIFY_CODE_PREFIX + phone);
        //如果验证码已经存在时
        if (StrUtil.isNotEmpty(existedSmsCode)) {
            //获取已经存在的验证码的过期时间
            Long expireTime = redisTemplate.opsForValue().getOperations().getExpire(REDIS_VERIFY_CODE_PREFIX + phone);
            //计算验证码已过去的时间
            long passedTime = 60 * 5 - expireTime;
            //验证码存在不超过一分钟，不得再次获取验证码
            if(passedTime <= 60) return "操作过于频繁";
        }
        //如果验证码已经存在超过1分钟或redis中不存在该手机号的验证码，生成并发送一个验证码
        String verifyCode = SMSUtil.sendSMS(phone);
        if (verifyCode.equals("error")) return "系统发生错误";
        //将生成的验证码存入redis，设置过期时间为5分钟
        redisTemplate.opsForValue().set(REDIS_VERIFY_CODE_PREFIX + phone,verifyCode,5, TimeUnit.MINUTES);
        return "发送成功";
    }

    @Override
    public String login(LoginUser loginUser) {
        String token = "";
        //根据登录用户类型调用不同service
        if (loginUser.getUserType() == 0) {
            token = volunteersInfoService.login(loginUser);
        } else if (loginUser.getUserType() == 1) {
            token = teamInfoService.login(loginUser);
        } else if (loginUser.getUserType() == 2) {
            token = unitInfoService.login(loginUser);
        } else {
            token = adminInfoService.login(loginUser);
        }
        return token;
    }

    @Override
    public String register(RegisterUser registerUser) {
        //首先校验该注册用户的手机验证码
        String verifyCode = redisTemplate.opsForValue().get(REDIS_VERIFY_CODE_PREFIX + registerUser.getPersonalPhone());
        if (StrUtil.isEmpty(verifyCode)) {
            return "验证码不存在或已过期";
        }
        if (!registerUser.getVerifyCode().equals(verifyCode)) {
            return "验证码不正确";
        }
        Integer registerType = Convert.toInt(registerUser.getRegisterType());
        Integer registerResult = -1;
        if (registerType == 0) {
            registerResult = volunteersInfoService.register(registerUser);
        } else if (registerType == 1) {
            registerResult = teamInfoService.register(registerUser);
        } else if (registerType == 2) {
            registerResult = unitInfoService.register(registerUser);
        }
        if (registerResult > 0) {
            redisTemplate.delete(REDIS_VERIFY_CODE_PREFIX + registerUser.getPersonalPhone());
            return "注册成功";
        }
        return "发生错误，注册失败";
    }

    @Override
    public String uploadImage(MultipartFile multipartFile) {
        //旧文件名
        String filename = multipartFile.getOriginalFilename();
        //后缀名
        assert filename != null;
        String suffix = filename.substring(filename.lastIndexOf("."));
        //以时间戳重命名
        String newFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis())))) + suffix;
        //腾讯云存储桶名称
        String bucketName = "";
        //获取到cos客户端
        COSClient cosClient = TxCOSUtil.initCOSClient();
        File localFile = null;
        try {
            localFile = File.createTempFile("temp",null);
            multipartFile.transferTo(localFile);
            //指定要上传到COS上的路径
            String key = "" + newFileName;
            //创建一个上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            //上传成功，返回路径，方便前端展示
            if (StrUtil.isNotEmpty(putObjectResult.getETag())) {
                return "" + newFileName;
            }
            return "上传失败";
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public boolean removeImage(String pictureUrl) {
        COSClient cosClient = TxCOSUtil.initCOSClient();
        try {
            cosClient.deleteObject("","" + pictureUrl.substring(pictureUrl.lastIndexOf("/")));
            return true;
        } catch (CosClientException e) {
            e.printStackTrace();
            return false;
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public Map<Object, Object> getUserProfile(Integer userId, Integer userType) {
        Map<Object, Object> profile = null;
        if (userType == 0) {
            profile = volunteersInfoService.queryVolunteerProfile(userId);
        } else if (userType == 1) {
            profile = teamInfoService.queryTeamProfile(userId);
        } else if (userType == 2) {
            profile = unitInfoService.queryUnitProfile(userId);
        } else {
            profile = adminInfoService.queryAdminProfile(userId);
        }
        return profile;
    }

    @Override
    public String getUserNickName(Integer userId, Integer userType) {
        String nickName = "";
        if (userType == 0) {
            nickName = volunteersInfoService.getVolunteerNickName(userId);
        } else if (userType == 1) {
            nickName = teamInfoService.getTeamNickName(userId);
        } else if (userType == 2) {
            nickName = unitInfoService.getUnitNickName(userId);
        } else {
            nickName = adminInfoService.getAdminNickName(userId);
        }
        return nickName;
    }

    @Override
    public String updateUser(UpdateUser updateUser) {
        String newToken = "";
        if (updateUser.getUserType() == 0) {
            newToken = volunteersInfoService.updateInfo(updateUser);
        } else if (updateUser.getUserType() == 1) {
            newToken = teamInfoService.updateInfo(updateUser);
        } else if (updateUser.getUserType() == 2) {
            newToken = unitInfoService.updateInfo(updateUser);
        } else {
            newToken = adminInfoService.updateInfo(updateUser);
        }
        return newToken;
    }

    @Override
    public String updateHead(UpdateHeader updateHeader) {
        //判断验证码是否正确
        String verifyCode = redisTemplate.opsForValue().get(REDIS_VERIFY_CODE_PREFIX + updateHeader.getPersonalPhone());
        if (StrUtil.isEmpty(verifyCode)) {
            return "验证码不存在或已过期";
        }
        if (!updateHeader.getVerifyCode().equals(verifyCode)) {
            return "验证码不正确";
        }
        //验证码相等，删除验证码
        redisTemplate.delete(REDIS_VERIFY_CODE_PREFIX + updateHeader.getPersonalPhone());
        boolean isSuccess = false;
        if (updateHeader.getUserType() == 1) {
            isSuccess = teamInfoService.updateHead(updateHeader);
        } else if (updateHeader.getUserType() == 2) {
            isSuccess = unitInfoService.updateHead(updateHeader);
        }
        return isSuccess ? "success" : "failure";
    }

    @Override
    public String modifyPassword(ModifyPassword modifyPassword) {
        String result = "";
        if (modifyPassword.getUserType() == 0) {
            result = volunteersInfoService.modifyPassword(modifyPassword);
        } else if (modifyPassword.getUserType() == 1) {
            result = teamInfoService.modifyPassword(modifyPassword);
        } else if (modifyPassword.getUserType() == 2) {
            result = unitInfoService.modifyPassword(modifyPassword);
        } else {
            result = adminInfoService.modifyPassword(modifyPassword);
        }
        return result;
    }

    @Override
    public boolean changeUserStatus(Integer userId, Integer userType, Integer changeStatus) {
        boolean changeSuccess = false;
        if (userType == 0) {
            changeSuccess = volunteersInfoService.changeStatus(userId, changeStatus);
        } else if (userType == 1) {
            changeSuccess = teamInfoService.changeStatus(userId, changeStatus);
        } else if (userType == 2) {
            changeSuccess = unitInfoService.changeStatus(userId, changeStatus);
        }
        return changeSuccess;
    }

    @Override
    public boolean deleteAccount(List<Integer> userIds, Integer userType) {
        boolean isSuccess = false;
        if (userType == 0) {
            isSuccess = volunteersInfoService.deleteAccount(userIds);
        } else if (userType == 1) {
            isSuccess = teamInfoService.deleteAccount(userIds);
        } else if (userType == 2) {
            isSuccess = unitInfoService.deleteAccount(userIds);
        }
        return isSuccess;
    }
}
