package com.xk.volunteer.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xk.volunteer.controller.pojo.ProvinceVolunteerCount;
import org.apache.ibatis.annotations.Param;

import com.xk.volunteer.entity.VolunteersInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Administrator
* @description 针对表【t_volunteers_info(志愿者信息表)】的数据库操作Mapper
* @createDate 2022-07-22 16:26:10
* @Entity com.xk.volunteer.entity.VolunteersInfo
*/
@Repository
public interface VolunteersInfoMapper extends BaseMapper<VolunteersInfo> {
    Long selectTotalHours();

    List<VolunteersInfo> selectVolunteersIdAndVolunteersUsernameAndVolunteersHoursOrderByVolunteersHoursDesc();

    VolunteersInfo selectOneByVolunteersPhoneAndVolunteersStatus(@Param("volunteersPhone") String volunteersPhone, @Param("volunteersStatus") Integer volunteersStatus);

    VolunteersInfo selectOneByVolunteersEmailAndVolunteersPasswordAndVolunteersStatus(@Param("volunteersEmail") String volunteersEmail, @Param("volunteersPassword") String volunteersPassword, @Param("volunteersStatus") Integer volunteersStatus);

    VolunteersInfo selectOneByVolunteersIdAndVolunteersStatus(@Param("volunteersId") Integer volunteersId, @Param("volunteersStatus") Integer volunteersStatus);

    List<ProvinceVolunteerCount> selectProvinceVolunteerCount();
}




