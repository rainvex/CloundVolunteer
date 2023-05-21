package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.JoinInfo;
import com.xk.volunteer.entity.TeamInfo;
import com.xk.volunteer.entity.VolunteersInfo;
import com.xk.volunteer.mapper.ActivityInfoMapper;
import com.xk.volunteer.mapper.TeamInfoMapper;
import com.xk.volunteer.mapper.VolunteersInfoMapper;
import com.xk.volunteer.service.JoinInfoService;
import com.xk.volunteer.mapper.JoinInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author Administrator
* @description 针对表【t_join_info(志愿者成功参加志愿活动的信息表)】的数据库操作Service实现
* @createDate 2022-07-22 16:26:09
*/
@Service
@Transactional
public class JoinInfoServiceImpl extends ServiceImpl<JoinInfoMapper, JoinInfo>
    implements JoinInfoService{

    @Autowired
    private JoinInfoMapper joinInfoMapper;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private TeamInfoMapper teamInfoMapper;

    @Override
    public List<JoinInfo> getVolunteersByActivityJoin(Integer activityId) {
        List<JoinInfo> joinInfoList = joinInfoMapper.selectJoinByVolunteerStatus(activityId);
        if (joinInfoList.size() > 0) {
            joinInfoList.forEach(joinInfo -> {
                joinInfo.setVolunteersInfo(volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>()
                        .eq("VOLUNTEERS_ID",joinInfo.getFkJoinVolunteer())));
            });
        }
        return joinInfoList;
    }

    @Override
    public boolean completeActivity(Integer activityId, Integer userId) {
        int update = joinInfoMapper.update(null, new UpdateWrapper<JoinInfo>()
                .eq("FK_JOIN_ACTIVITY", activityId)
                .eq("FK_JOIN_VOLUNTEER",userId)
                .set("JOIN_STATUS",1));
        if (update > 0) {
            //完成志愿活动，增加该志愿者志愿时长和志愿活动数
            VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(userId);
            volunteersInfo.setVolunteersActivecount(volunteersInfo.getVolunteersActivecount() + 1);
            //计算对应志愿活动的活动时间有多少小时
            ActivityInfo activityInfo = activityInfoMapper.selectById(activityId);
            String activityContinue = activityInfo.getActivityContinue();
            String startTime = activityContinue.substring(0, activityContinue.indexOf("至") - 1);
            String endTime = activityContinue.substring(activityContinue.indexOf("至") + 2);
            int between = (int)DateUtil.between(DateUtil.parseDate(startTime), DateUtil.parseDate(endTime), DateUnit.HOUR);
            volunteersInfo.setVolunteersHours(volunteersInfo.getVolunteersHours() + between);
            volunteersInfoMapper.updateById(volunteersInfo);
            //将该志愿者的志愿活动数和志愿时长加到其所在团队（如果其有团队的话）
            if (volunteersInfo.getFkVolunteersTeam() != null) {
                TeamInfo teamInfo = teamInfoMapper.selectOne(new UpdateWrapper<TeamInfo>()
                        .eq("TEAM_ID", volunteersInfo.getFkVolunteersTeam()));
                teamInfo.setTeamActivecount(teamInfo.getTeamActivecount() + 1);
                teamInfo.setTeamHours(teamInfo.getTeamHours() + between);
                teamInfoMapper.updateById(teamInfo);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteJoins(List<Integer> joinIds) {
        return joinInfoMapper.deleteBatchIds(joinIds) > 0;
    }
}




