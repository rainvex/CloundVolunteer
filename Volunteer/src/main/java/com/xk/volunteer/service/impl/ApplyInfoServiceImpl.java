package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.entity.*;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.ApplyInfoService;
import com.xk.volunteer.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Administrator
* @description 针对表【t_apply_info(申请信息表（志愿者申请加入志愿者团队、志愿者申请参加志愿活动）)】的数据库操作Service实现
* @createDate 2022-08-05 14:52:01
*/
@Service
@Transactional
public class ApplyInfoServiceImpl extends ServiceImpl<ApplyInfoMapper, ApplyInfo>
    implements ApplyInfoService{

    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private TeamInfoMapper teamInfoMapper;
    @Autowired
    private UnitInfoMapper unitInfoMapper;
    @Autowired
    private JoinInfoMapper joinInfoMapper;
    @Autowired
    private MessageInfoService messageInfoService;

    @Override
    public Integer insertApply(ApplyInfo applyInfo) {
        applyInfo.setApplyTime(DateUtil.now());
        //如果是申请参加志愿活动，则查询到该活动的发布者
        if (applyInfo.getApplyType() == 0) {
            applyInfo.setFkAuditorId(activityInfoMapper.selectById(applyInfo.getFkAppliedActivity()).getFkActivityPublisher());
            //调用发起消息通知的方法
            messageInfoService.insertMessage("申请参加活动审核通知","",applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkAppliedActivity());
        }
        //如果是申请加入志愿者团队，则将申请的志愿者的isApplyTeam设为1
        if (applyInfo.getApplyType() == 1) {
            VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(applyInfo.getFkApplicantId());
            volunteersInfo.setVolunteersIsapplyteam(1);
            volunteersInfoMapper.updateById(volunteersInfo);
            //调用发起消息通知的方法
            messageInfoService.insertMessage("申请加入团队审核通知","",applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),null);
        }
        return applyInfoMapper.insert(applyInfo);
    }

    @Override
    public Map<Object,Object> getActivityApply(Integer userId, Integer userType, Integer currentPage) {
        List<ApplyInfo> applyInfoList = new ArrayList<>();
        if (userType == 0) {
            applyInfoList = applyInfoMapper.selectApplyByActivityShow(userId,userType,0,(currentPage - 1) * 12);
        } else if (userType == 2) {
            QueryWrapper<ApplyInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("FK_APPLICANT_ID",userId)
                    .eq("FK_APPLICANT_TYPE",2)
                    .eq("APPLY_TYPE",2)
                    .orderByDesc("APPLY_TIME");
            applyInfoList = applyInfoMapper.selectPage(new Page<>(currentPage, 12),wrapper).getRecords();
        }
        if (applyInfoList.size() > 0) {
            applyInfoList.forEach(applyInfo-> {
                ActivityInfo activityInfo = activityInfoMapper.selectById(applyInfo.getFkAppliedActivity());
                //判断当前时间是否已超过报名截止时间
                boolean isExpired = DateUtil.parseDate(DateUtil.now()).after(DateUtil.parseDate(activityInfo.getActivityCutoff()));
                activityInfo.setExpired(isExpired);
                applyInfo.setAppliedActive(activityInfo);
                //如果当前时间已超过活动报名截止时间且该申请信息还是待审核状态，则设置该申请审核未通过
                if (isExpired && applyInfo.getApplyStatus() == 0) {
                    applyInfo.setApplyStatus(2);
                    applyInfo.setApplyAudittime(DateUtil.now());
                    applyInfoMapper.updateById(applyInfo);
                }
            });
        }
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("applyList",applyInfoList);
        map.put("total",applyInfoList.size());
        return map;
    }

    @Override
    public Boolean getIsApplied(Integer userId, Integer activeId) {
        QueryWrapper<ApplyInfo> wrapper = new QueryWrapper<>();
        //查询是否已申请志愿活动
        wrapper.eq("APPLY_TYPE",0)
                .eq("FK_APPLICANT_ID",userId)
                .eq("FK_APPLICANT_TYPE",0)
                .eq("FK_APPLIED_ACTIVITY",activeId);
        return applyInfoMapper.exists(wrapper);
    }

    @Override
    public Integer cancelApplyActivity(Integer userId, Integer userType, Integer activeId) {
        QueryWrapper<ApplyInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("FK_APPLICANT_ID",userId);
        wrapper.eq("FK_APPLICANT_TYPE",userType);
        wrapper.eq("FK_APPLIED_ACTIVITY",activeId);
        if (userType == 0) wrapper.eq("APPLY_TYPE",0);
        else if (userType == 2) wrapper.eq("APPLY_TYPE",2);
        int delete = applyInfoMapper.delete(wrapper);
        if (delete > 0 && userType == 0) {
            //增加该志愿者的违约次数
            VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(userId);
            volunteersInfo.setVolunteersBreak(volunteersInfo.getVolunteersBreak() + 1);
            return volunteersInfoMapper.updateById(volunteersInfo);
        }
        return delete;
    }

    @Override
    public Map<Object, Object> getAuditInfo(Integer userId, Integer userType, Integer applyType, Page<ApplyInfo> page) {
        QueryWrapper<ApplyInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("FK_AUDITOR_ID",userId);
        wrapper.eq("FK_AUDITOR_TYPE",userType);
        //根据用户类型查询不同的申请信息，以便审核
        //获取申请类型为申请加入团队的信息
        if (userType == 1) wrapper.eq("APPLY_TYPE",1);
        //获取申请类型为申请参加志愿活动的信息
        else if (userType == 2) wrapper.eq("APPLY_TYPE",0);
        //获取申请类型为活动发布、志愿者团队注册、志愿单位注册的信息
        else if (userType == 3) {
            if (applyType == 2) {
                wrapper.eq("APPLY_TYPE",2);
            } else if (applyType == 5) {
                wrapper.and(i -> i.eq("APPLY_TYPE",3)
                                    .or()
                                    .eq("APPLY_TYPE",4));
            }
        }
        wrapper.orderByDesc("APPLY_TIME");
        Page<ApplyInfo> applyInfoPage = applyInfoMapper.selectPage(page, wrapper);
        long total = applyInfoPage.getTotal();
        List<ApplyInfo> applyInfoList = applyInfoPage.getRecords();
        if (total > 0) {
            //total大于0，说明有需要该用户进行审核的申请信息，此时再根据用户类型对申请所需要的信息进行不同处理
            //在前端我们还需要展示出申请人的用户名，则在这里我们需要根据申请类型不同查询到对应不同类型申请人的id及用户名
            //如果用户类型为志愿者团队或志愿单位，则需要其审核的申请信息类型即为申请加入团队或申请参加活动，而发起申请的对象均为志愿者
            if (userType == 1 || userType == 2) {
                applyInfoList.forEach(applyInfo -> {
                    applyInfo.setApplierObject(volunteersInfoMapper.selectOne(
                            new QueryWrapper<VolunteersInfo>()
                                    .select("VOLUNTEERS_ID,VOLUNTEERS_USERNAME,VOLUNTEERS_TYPE")
                                    .eq("VOLUNTEERS_ID",applyInfo.getFkApplicantId())));
                    //如果申请类型是申请参加活动，则判断当前时间是否超过活动报名截止时间且还未进行审核，若是，则将该申请设置为审核未通过
                    if (applyInfo.getApplyType() == 0) {
                        String activityCutoff = activityInfoMapper.selectById(applyInfo.getFkAppliedActivity()).getActivityCutoff();
                        if (DateUtil.parseDate(DateUtil.now()).after(DateUtil.parseDate(activityCutoff)) && applyInfo.getApplyStatus() == 0) {
                            applyInfo.setApplyStatus(2);
                            applyInfo.setApplyAudittime(DateUtil.now());
                            applyInfoMapper.updateById(applyInfo);
                        }
                    } else if (applyInfo.getApplyType() == 1) {
                        //如果申请类型是申请加入团队，则判断当前时间是否超过申请时间5天且还未进行审核，若是，则将该申请设置为审核未通过
                        if ((DateUtil.between(DateUtil.parseDate(DateUtil.now()),DateUtil.parseDate(applyInfo.getApplyTime()), DateUnit.DAY) >= 5) && applyInfo.getApplyStatus() == 0) {
                            applyInfo.setApplyStatus(2);
                            applyInfo.setApplyAudittime(DateUtil.now());
                            applyInfoMapper.updateById(applyInfo);
                        }
                    }
                });
            }
            //如果用户类型是管理员，则需要其审核的申请信息类型为活动发布申请（志愿单位）、志愿者团队注册申请（志愿者团队）、志愿单位注册申请（志愿单位）
            else if (userType == 3) {
                applyInfoList.forEach(applyInfo -> {
                    //如果该申请信息类型为发布志愿活动，判断当前时间是否超过报名截止时间且还未审核，若是则设置为审核不通过
                    if (applyInfo.getApplyType() == 2) {
                        applyInfo.setAppliedActive(activityInfoMapper.selectOne(new QueryWrapper<ActivityInfo>()
                                .select("ACTIVITY_ID,ACTIVITY_TITLE")
                                .eq("ACTIVITY_ID",applyInfo.getFkAppliedActivity())));
                        String activityCutoff = activityInfoMapper.selectById(applyInfo.getFkAppliedActivity()).getActivityCutoff();
                        if (DateUtil.parseDate(DateUtil.now()).after(DateUtil.parseDate(activityCutoff)) && applyInfo.getApplyStatus() == 0) {
                            applyInfo.setApplyStatus(2);
                            applyInfo.setApplyAudittime(DateUtil.now());
                            applyInfoMapper.updateById(applyInfo);
                        }
                        //设置申请人信息
                        applyInfo.setApplierObject(unitInfoMapper.selectOne(
                                new QueryWrapper<UnitInfo>()
                                        .select("UNIT_ID,UNIT_NAME,UNIT_TYPE")
                                        .eq("UNIT_ID",applyInfo.getFkApplicantId())));
                    } else if (applyInfo.getApplyType() == 3) {
                        //设置申请人
                        applyInfo.setApplierObject(teamInfoMapper.selectOne(
                                new QueryWrapper<TeamInfo>()
                                        .select("TEAM_ID,TEAM_NAME,TEAM_TYPE")
                                        .eq("TEAM_ID",applyInfo.getFkApplicantId())));
                    } else if (applyInfo.getApplyType() == 4) {
                        //设置申请人信息
                        applyInfo.setApplierObject(unitInfoMapper.selectOne(
                                new QueryWrapper<UnitInfo>()
                                        .select("UNIT_ID,UNIT_NAME,UNIT_TYPE")
                                        .eq("UNIT_ID",applyInfo.getFkApplicantId())));
                    }
                });
            }
        }
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("auditList",applyInfoList);
        map.put("total",total);
        return map;
    }

    @Override
    public ApplyInfo queryApplyById(Integer applyId) {
        ApplyInfo applyInfo = applyInfoMapper.selectById(applyId);
        if (applyInfo != null) {
            if (applyInfo.getApplyType() == 0 || applyInfo.getApplyType() == 2) {
                applyInfo.setAppliedActive(activityInfoMapper.selectOne(
                        new QueryWrapper<ActivityInfo>()
                                .select("ACTIVITY_ID,ACTIVITY_TITLE")
                                .eq("ACTIVITY_ID",applyInfo.getFkAppliedActivity())
                ));
            }
            if (applyInfo.getApplyType() == 0 || applyInfo.getApplyType() == 1) {
                applyInfo.setApplierObject(volunteersInfoMapper.selectOne(
                        new QueryWrapper<VolunteersInfo>()
                                .select("VOLUNTEERS_ID,VOLUNTEERS_USERNAME,VOLUNTEERS_TYPE")
                                .eq("VOLUNTEERS_ID",applyInfo.getFkApplicantId())));
            } else if (applyInfo.getApplyType() == 2 || applyInfo.getApplyType() == 4) {
                applyInfo.setApplierObject(unitInfoMapper.selectOne(
                        new QueryWrapper<UnitInfo>()
                                .select("UNIT_ID,UNIT_NAME,UNIT_TYPE")
                                .eq("UNIT_ID",applyInfo.getFkApplicantId())));
            } else if (applyInfo.getApplyType() == 3) {
                applyInfo.setApplierObject(teamInfoMapper.selectOne(
                        new QueryWrapper<TeamInfo>()
                                .select("TEAM_ID,TEAM_NAME,TEAM_TYPE")
                                .eq("TEAM_ID",applyInfo.getFkApplicantId())));
            }
            return applyInfo;
        }
        return null;
    }

    @Override
    public boolean deleteApply(List<Integer> applyIds) {
        return applyInfoMapper.deleteBatchIds(applyIds) > 0;
    }

    @Override
    public boolean auditApply(Integer applyId, String auditCommand) {
        //首先将审核对应的申请信息状态进行修改
        UpdateWrapper<ApplyInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("APPLY_ID",applyId);
        wrapper.set("APPLY_AUDITTIME",DateUtil.now());
        if (StrUtil.equals(auditCommand,"审核通过")) {
            wrapper.set("APPLY_STATUS",1);
        } else if (StrUtil.equals(auditCommand,"审核未通过")) {
            wrapper.set("APPLY_STATUS",2);
        }
        //其他业务操作
        if (applyInfoMapper.update(null,wrapper) > 0) {
            //审核完成后，需要根据申请信息的类型做相应的业务
            ApplyInfo applyInfo = applyInfoMapper.selectOne(new QueryWrapper<ApplyInfo>().eq("APPLY_ID", applyId));
            if (StrUtil.equals(auditCommand,"审核通过")) {
                //审核通过，根据申请信息类型不同做相应改变
                if (applyInfo.getApplyType() == 0) {
                    //申请类型为申请参加活动，审核通过后需要将被申请的活动和申请人的id记录插入到joinInfo表中
                    JoinInfo joinInfo = new JoinInfo();
                    joinInfo.setFkJoinActivity(applyInfo.getFkAppliedActivity());
                    joinInfo.setFkJoinVolunteer(applyInfo.getFkApplicantId());
                    //调用发起消息通知的方法
                    messageInfoService.insertMessage("申请参加活动审核结果通知",auditCommand,applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),applyInfo.getFkAppliedActivity());
                    return joinInfoMapper.insert(joinInfo) > 0;
                } else if (applyInfo.getApplyType() == 1) {
                    //申请类型为申请加入志愿者团队，审核通过后需要将志愿者正在申请加入团队的状态设置0，且设置其所成功加入团队的id
                    //并将该志愿者团队的人数加1，将该志愿者的活动时长和活动数加到志愿者团队总数中
                    VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(applyInfo.getFkApplicantId());
                    volunteersInfo.setVolunteersIsapplyteam(0);
                    volunteersInfo.setFkVolunteersTeam(applyInfo.getFkAuditorId());
                    volunteersInfoMapper.updateById(volunteersInfo);
                    TeamInfo teamInfo = teamInfoMapper.selectById(applyInfo.getFkAuditorId());
                    teamInfo.setTeamHours(teamInfo.getTeamHours() + volunteersInfo.getVolunteersHours());
                    teamInfo.setTeamActivecount(teamInfo.getTeamActivecount() + volunteersInfo.getVolunteersActivecount());
                    teamInfo.setTeamCount(teamInfo.getTeamCount() + 1);
                    //调用发起消息通知的方法
                    messageInfoService.insertMessage("申请加入团队审核结果通知",auditCommand,applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),null);
                    return teamInfoMapper.updateById(teamInfo) > 0;
                } else if (applyInfo.getApplyType() == 2) {
                    //申请类型为发布志愿活动，审核通过后需要将该活动的show状态设置为1，将发布者的发布活动数加1
                    ActivityInfo activityInfo = activityInfoMapper.selectById(applyInfo.getFkAppliedActivity());
                    activityInfo.setActivityShow(1);
                    activityInfoMapper.updateById(activityInfo);
                    UnitInfo unitInfo = unitInfoMapper.selectById(activityInfo.getFkActivityPublisher());
                    unitInfo.setUnitPubliccount(unitInfo.getUnitPubliccount() + 1);
                    //调用发起消息通知的方法
                    messageInfoService.insertMessage("申请发布活动审核结果通知",auditCommand,applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),applyInfo.getFkAppliedActivity());
                    return unitInfoMapper.updateById(unitInfo) > 0;
                } else if (applyInfo.getApplyType() == 3) {
                    //申请类型为志愿者团队注册，审核通过后需要将申请注册的志愿者团队状态设为1
                    return teamInfoMapper.update(null,new UpdateWrapper<TeamInfo>()
                            .set("TEAM_STATUS",1)
                            .eq("TEAM_ID",applyInfo.getFkApplicantId())) > 0;
                } else if (applyInfo.getApplyType() == 4) {
                    //申请类型为志愿单位注册，审核通过后需要将申请注册的志愿单位状态设为1
                    return unitInfoMapper.update(null,new UpdateWrapper<UnitInfo>()
                            .set("UNIT_STATUS",1)
                            .eq("UNIT_ID",applyInfo.getFkApplicantId())) > 0;
                }
            } else if (StrUtil.equals(auditCommand,"审核未通过")) {
                //审核不通过，仍需根据申请信息类型不同做相应改变
                if (applyInfo.getApplyType() == 0) {
                    //调用发起消息通知的方法
                    messageInfoService.insertMessage("申请参加活动审核结果通知",auditCommand,applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),applyInfo.getFkAppliedActivity());
                    return true;
                } else if (applyInfo.getApplyType() == 1) {
                    //调用发起消息通知的方法
                    messageInfoService.insertMessage("申请加入团队审核结果通知",auditCommand,applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),null);
                    //申请类型为申请加入志愿者团队，审核不通过也需要将志愿者正在申请加入团队的状态设置0，让其能申请加入其他团队
                    return volunteersInfoMapper.update(null,new UpdateWrapper<VolunteersInfo>()
                            .set("VOLUNTEERS_ISAPPLYTEAM",0)
                            .eq("VOLUNTEERS_ID",applyInfo.getFkApplicantId())) > 0;
                } else if (applyInfo.getApplyType() == 2) {
                    //调用发起消息通知的方法
                    messageInfoService.insertMessage("申请发布活动审核结果通知",auditCommand,applyInfo.getFkAuditorId(),applyInfo.getFkAuditorType(),applyInfo.getFkApplicantId(),applyInfo.getFkApplicantType(),applyInfo.getFkAppliedActivity());
                    return true;
                } else if (applyInfo.getApplyType() == 3) {
                    //暂时无需操作
                    return true;
                } else if (applyInfo.getApplyType() == 4) {
                    //暂时无需操作
                    return true;
                }
            }
        }
        return false;
    }
}




