package com.xk.volunteer.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.xk.volunteer.entity.JoinInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Administrator
* @description 针对表【t_join_info(志愿者成功参加志愿活动的信息表)】的数据库操作Mapper
* @createDate 2022-07-22 16:26:09
* @Entity com.xk.volunteer.entity.JoinInfo
*/
@Repository
public interface JoinInfoMapper extends BaseMapper<JoinInfo> {
    List<JoinInfo> selectJoinByVolunteerStatus(@Param("fkJoinActivity") Integer fkJoinActivity);

    List<JoinInfo> selectJoinByActivityShowPage(@Param("fkJoinVolunteer") Integer userId, @Param("startIndex") Integer startIndex);
}




