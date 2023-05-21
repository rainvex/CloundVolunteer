package com.xk.volunteer.mapper;

import com.xk.volunteer.entity.StarInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_star_info(志愿活动被收藏的信息表)】的数据库操作Mapper
* @createDate 2022-07-22 16:26:10
* @Entity com.xk.volunteer.entity.StarInfo
*/
@Repository
public interface StarInfoMapper extends BaseMapper<StarInfo> {
    List<StarInfo> selectStarByActivityShow(@Param("userId") Integer userId,@Param("startIndex") Integer startIndex);
}




