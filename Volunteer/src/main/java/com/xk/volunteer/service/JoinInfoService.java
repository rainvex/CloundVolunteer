package com.xk.volunteer.service;

import com.xk.volunteer.entity.JoinInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xk.volunteer.entity.VolunteersInfo;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_join_info(志愿者成功参加志愿活动的信息表)】的数据库操作Service
* @createDate 2022-07-22 16:26:09
*/
public interface JoinInfoService extends IService<JoinInfo> {
    List<JoinInfo> getVolunteersByActivityJoin(Integer activityId);

    boolean completeActivity(Integer activityId, Integer userId);

    boolean deleteJoins(List<Integer> joinIds);
}
