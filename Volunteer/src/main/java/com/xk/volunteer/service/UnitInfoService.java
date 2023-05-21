package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.controller.pojo.*;
import com.xk.volunteer.entity.UnitInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_unit_info】的数据库操作Service
* @createDate 2022-07-22 16:26:10
*/
public interface UnitInfoService extends IService<UnitInfo> {
    Long getUnitCount();

    Integer register(RegisterUser registerUser);

    String login(LoginUser loginUser);

    Map<Object,Object> queryUnitProfile(Integer userId);

    String getUnitNickName(Integer userId);

    Page<UnitInfo> getUnitByCondition(String province, String city, String classic, Page<UnitInfo> page);

    String updateInfo(UpdateUser updateUser);

    boolean updateHead(UpdateHeader updateHeader);

    String modifyPassword(ModifyPassword modifyPassword);

    boolean changeStatus(Integer userId, Integer status);

    boolean deleteAccount(List<Integer> userIds);
}
