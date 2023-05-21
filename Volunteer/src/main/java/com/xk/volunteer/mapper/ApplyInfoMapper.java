package com.xk.volunteer.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ApplyInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_apply_info(申请信息表（志愿者申请加入志愿者团队、志愿者申请参加志愿活动）)】的数据库操作Mapper
* @createDate 2022-08-05 14:52:01
* @Entity com.xk.volunteer.entity.ApplyInfo
*/
@Repository
public interface ApplyInfoMapper extends BaseMapper<ApplyInfo> {
    List<ApplyInfo> selectApplyByActivityShow(@Param("userId") Integer userId,
                                              @Param("userType") Integer userType,
                                              @Param("applyType") Integer applyType,
                                              @Param("startIndex") Integer startIndex);
}




