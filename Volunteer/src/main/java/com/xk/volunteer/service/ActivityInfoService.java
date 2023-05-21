package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ActivityInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xk.volunteer.entity.JoinInfo;
import com.xk.volunteer.entity.VolunteersInfo;

import java.util.List;
import java.util.Map;


/**
* @author Administrator
* @description 针对表【t_activity_info(志愿活动信息表)】的数据库操作Service
* @createDate 2022-07-22 16:26:09
*/
public interface ActivityInfoService extends IService<ActivityInfo> {
    Long getActivityCount();

    List<ActivityInfo> getActivityMostApplyAndPublic();

    Map<Object,Object> getActivityByCondition(String province, String city, String classic, Page<ActivityInfo> page);

    ActivityInfo getActivityById(Integer id);

    Integer publicActivity(ActivityInfo activityInfo);

    Map<Object,Object> getActivityRecord(Integer userId, Integer userType, Integer currentPage);

    boolean changeActivityShow(Integer activityId, Integer changeShow);

    boolean deleteActivityRecord(List<Integer> activityIds);
}
