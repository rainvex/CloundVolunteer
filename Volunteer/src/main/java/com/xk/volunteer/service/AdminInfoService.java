package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.controller.pojo.LoginUser;
import com.xk.volunteer.controller.pojo.ModifyPassword;
import com.xk.volunteer.controller.pojo.UpdateUser;
import com.xk.volunteer.entity.AdminInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xk.volunteer.entity.InformInfo;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_admin_info(管理员信息表)】的数据库操作Service
* @createDate 2022-07-22 16:26:09
*/
public interface AdminInfoService extends IService<AdminInfo> {

    String login(LoginUser loginUser);

    Map<Object,Object> queryAdminProfile(Integer userId);

    String getAdminNickName(Integer userId);

    String updateInfo(UpdateUser updateUser);

    String modifyPassword(ModifyPassword modifyPassword);

    Map<Object, Object> home();

    List getUserList(Integer userType, Integer currentPage);
}
