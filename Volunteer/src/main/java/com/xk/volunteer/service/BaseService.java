package com.xk.volunteer.service;

import com.xk.volunteer.controller.pojo.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/28
 * @Name BaseService
 */
public interface BaseService {
    //用于jwt拦截时根据token携带数据查询该用户的密码作为jwt的验签标识
    String getPasswordByToken(Integer userId, Integer userType);

    Map<String, Object> index();

    //获取验证码
    String getVerifyCode(String phone,Integer userType, String verifyLogo);

    //用户登录
    String login(LoginUser loginUser);

    //用户注册
    String register(RegisterUser registerUser);

    //上传图片
    String uploadImage(MultipartFile multipartFile);

    //移除图片
    boolean removeImage(String pictureUrl);

    //获取用户信息
    Map<Object, Object> getUserProfile(Integer userId, Integer userType);

    //获取用户名
    String getUserNickName(Integer userId,Integer userType);

    //更新用户信息
    String updateUser(UpdateUser updateUser);

    //更新志愿团队和志愿单位的负责人信息
    String updateHead(UpdateHeader updateHeader);

    //修改密码
    String modifyPassword(ModifyPassword modifyPassword);

    //修改用户账号启用状态
    boolean changeUserStatus(Integer userId, Integer userType, Integer changeStatus);

    //注销账号
    boolean deleteAccount(List<Integer> userIds, Integer userType);
}
