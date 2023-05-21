package com.xk.volunteer.mapper;

import com.xk.volunteer.entity.InformInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_inform_info】的数据库操作Mapper
* @createDate 2022-12-26 13:37:51
* @Entity com.xk.volunteer.entity.InformInfo
*/
public interface InformInfoMapper extends BaseMapper<InformInfo> {
    List<InformInfo> selectInformHighView();
}




