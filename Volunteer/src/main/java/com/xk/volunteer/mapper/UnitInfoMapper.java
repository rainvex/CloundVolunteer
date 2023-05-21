package com.xk.volunteer.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.xk.volunteer.entity.UnitInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Administrator
* @description 针对表【t_unit_info】的数据库操作Mapper
* @createDate 2022-07-22 16:26:10
* @Entity com.xk.volunteer.entity.UnitInfo
*/
@Repository
public interface UnitInfoMapper extends BaseMapper<UnitInfo> {

    UnitInfo selectOneByUnitHeadPhoneAndUnitStatus(@Param("unitHeadPhone") String unitHeadPhone, @Param("unitStatus") Integer unitStatus);

    UnitInfo selectOneByUnitEmailAndUnitPasswordAndUnitStatus(@Param("unitEmail") String unitEmail, @Param("unitPassword") String unitPassword, @Param("unitStatus") Integer unitStatus);

    String selectUnitNameByUnitId(@Param("unitId") Integer unitId);

    List<UnitInfo> selectMostApplyAndCountLimit();
}




