package com.xk.volunteer.mapper;
import com.xk.volunteer.controller.pojo.ActivityClassicCount;
import org.apache.ibatis.annotations.Param;

import com.xk.volunteer.entity.ActivityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_activity_info(志愿活动信息表)】的数据库操作Mapper
* @createDate 2022-07-22 16:26:09
* @Entity com.xk.volunteer.entity.ActivityInfo
*/
@Repository
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {
    List<ActivityInfo> selectMostApplyAndPublic();

    List<ActivityClassicCount> selectActivityClassicCount();
}




