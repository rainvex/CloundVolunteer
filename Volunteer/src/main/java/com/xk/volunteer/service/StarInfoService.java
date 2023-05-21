package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.StarInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_star_info(志愿活动被收藏的信息表)】的数据库操作Service
* @createDate 2022-07-22 16:26:10
*/
public interface StarInfoService extends IService<StarInfo> {
    Boolean getIsStared(Integer userId,Integer activeId);

    ActivityInfo starActivity(Integer userId, Integer activeId);

    Map<Object,Object> getAllStarByVolunteer(Integer userId, Integer currentPage);

    Integer cancelStar(Integer starId);
}
