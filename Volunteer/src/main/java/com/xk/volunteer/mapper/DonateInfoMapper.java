package com.xk.volunteer.mapper;

import com.xk.volunteer.entity.DonateInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Administrator
* @description 针对表【t_donate_info(捐赠信息表)】的数据库操作Mapper
* @createDate 2022-08-11 19:41:52
* @Entity com.xk.volunteer.entity.DonateInfo
*/
@Repository
public interface DonateInfoMapper extends BaseMapper<DonateInfo> {
    Long statisticsDonateCount();
}




