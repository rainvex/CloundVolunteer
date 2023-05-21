package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.controller.pojo.*;
import com.xk.volunteer.entity.TeamInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xk.volunteer.entity.VolunteersInfo;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_team_info】的数据库操作Service
* @createDate 2022-07-22 16:26:10
*/
public interface TeamInfoService extends IService<TeamInfo> {
    Long getTeamCount();

    Integer register(RegisterUser registerUser);

    String login(LoginUser loginUser);

    Map<Object,Object> queryTeamProfile(Integer userId);

    String getTeamNickName(Integer userId);

    List<TeamInfo> getMostFiveOrderByHours();

    Page<TeamInfo> getTeamByCondition(String province, String city, String classic, String serviceObj, Page<TeamInfo> page);

    String updateInfo(UpdateUser updateUser);

    boolean updateHead(UpdateHeader updateHeader);

    String modifyPassword(ModifyPassword modifyPassword);

    Map<Object,Object> getTeamInfo(Integer userId,Integer userType,Integer currentPage);

    boolean memberKickOrExit(List<Integer> volunteerIds, Integer teamId);

    boolean changeStatus(Integer userId, Integer status);

    boolean deleteAccount(List<Integer> userIds);
}
