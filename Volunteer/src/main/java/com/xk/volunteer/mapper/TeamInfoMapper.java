package com.xk.volunteer.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.xk.volunteer.entity.TeamInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author Administrator
* @description 针对表【t_team_info】的数据库操作Mapper
* @createDate 2022-07-22 16:26:10
* @Entity com.xk.volunteer.entity.TeamInfo
*/
@Repository
public interface TeamInfoMapper extends BaseMapper<TeamInfo> {

    TeamInfo selectOneByTeamHeadPhoneAndTeamStatus(@Param("teamHeadPhone") String teamHeadPhone, @Param("teamStatus") Integer teamStatus);

    TeamInfo selectOneByTeamEmailAndTeamPasswordAndTeamStatus(@Param("teamEmail") String teamEmail, @Param("teamPassword") String teamPassword, @Param("teamStatus") Integer teamStatus);

    TeamInfo selectTeamNameByTeamIdAndTeamStatus(@Param("teamId") Integer teamId, @Param("teamStatus") Integer teamStatus);

    List<TeamInfo> selectTeamIdAndTeamNameAndTeamHoursOrderByTeamHoursDesc();

    List<TeamInfo> selectMostApplyAndHourLimit();
}




