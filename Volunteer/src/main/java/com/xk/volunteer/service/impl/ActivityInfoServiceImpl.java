package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.entity.*;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.ActivityInfoService;
import com.xk.volunteer.service.ApplyInfoService;
import com.xk.volunteer.service.JoinInfoService;
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
* @description 针对表【t_activity_info(志愿活动信息表)】的数据库操作Service实现
* @createDate 2022-07-22 16:26:09
*/
@Service
@Transactional
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo>
    implements ActivityInfoService{

    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private StarInfoMapper starInfoMapper;
    @Autowired
    private UnitInfoMapper unitInfoMapper;
    @Autowired
    private JoinInfoService joinInfoService;
    @Autowired
    private JoinInfoMapper joinInfoMapper;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private ApplyInfoService applyInfoService;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private MessageInfoService messageInfoService;

    @Override
    public Long getActivityCount() {
        return activityInfoMapper.selectCount(new QueryWrapper<ActivityInfo>().eq("ACTIVITY_SHOW",1));
    }

    @Override
    public List<ActivityInfo> getActivityMostApplyAndPublic() {
        List<ActivityInfo> activityInfoList = activityInfoMapper.selectMostApplyAndPublic();
        if (activityInfoList.size() > 0) {
            activityInfoList.forEach(activityInfo -> {
                activityInfo.setPublisherUnitName(unitInfoMapper.selectById(activityInfo.getFkActivityPublisher()).getUnitName());
            });
        }
        return activityInfoList;
    }

    @Override
    public Map<Object,Object> getActivityByCondition(String province, String city, String classic, Page<ActivityInfo> page) {
        QueryWrapper<ActivityInfo> wrapper = new QueryWrapper<>();
        wrapper.select("ACTIVITY_ID,ACTIVITY_TITLE,ACTIVITY_BANNER,ACTIVITY_PLACE,ACTIVITY_PUBLIC,ACTIVITY_LIMIT,FK_ACTIVITY_PUBLISHER");
        wrapper.eq("ACTIVITY_SHOW",1);
        //当查询条件中省不为空
        if (StrUtil.isNotEmpty(province)) {
            StringBuffer area = new StringBuffer(province);
            //当查询条件中市不为空
            if (StrUtil.isNotEmpty(city))
                area.append(city);
            wrapper.likeRight("ACTIVITY_PLACE",area);
        }
        //当查询条件中分类不为空
        if (StrUtil.isNotEmpty(classic)) {
            wrapper.eq("ACTIVITY_CLASSIC",classic);
        }
        wrapper.orderByDesc("ACTIVITY_PUBLIC");
        Page<ActivityInfo> activityInfos = activityInfoMapper.selectPage(page,wrapper);
        List<ActivityInfo> activityList = activityInfos.getRecords();
        if (activityList.size() > 0) {
            activityList.forEach(activityInfo -> {
                //查询每个活动被收藏的数量
                Long starCount = starInfoMapper.selectCount(new QueryWrapper<StarInfo>().eq("FK_STAR_ACTIVITY",activityInfo.getActivityId()));
                activityInfo.setStaredCount(starCount);
                //查询每个活动的发布单位
                activityInfo.setPublisherUnitName(unitInfoMapper.selectUnitNameByUnitId(activityInfo.getFkActivityPublisher()));
                //查询已招募的人数
                activityInfo.setActivityApplied((long) joinInfoMapper.selectJoinByVolunteerStatus(activityInfo.getActivityId()).size());
            });
        }
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("activityList",activityList);
        map.put("total",activityInfos.getTotal());
        return map;
    }

    @Override
    public ActivityInfo getActivityById(Integer id) {
        ActivityInfo activityInfo = activityInfoMapper.selectById(id);
        if (activityInfo == null) return null;
        //查询发布单位名字
        String unitName = unitInfoMapper.selectUnitNameByUnitId(activityInfo.getFkActivityPublisher());
        activityInfo.setPublisherUnitName(unitName);
        //查询活动被收藏数
        activityInfo.setStaredCount(starInfoMapper.selectCount(new QueryWrapper<StarInfo>().eq("FK_STAR_ACTIVITY",id)));
        //查询申请参加该活动被审核通过的的志愿者
        List<JoinInfo> joinInfoList = joinInfoMapper.selectJoinByVolunteerStatus(id);
        //设置已申请参加该活动的人数
        activityInfo.setActivityApplied((long) joinInfoList.size());
        //判断当前时间是否已超过报名截止时间
        activityInfo.setExpired(DateUtil.parseDate(DateUtil.now()).after(DateUtil.parseDate(activityInfo.getActivityCutoff())));
        //如果查询出来的志愿者id集合长度不为0，则遍历该集合，根据id查询出对应志愿者的详细信息
        List<VolunteersInfo> volunteersList = new ArrayList<>();
        if (joinInfoList.size() > 0) {
            joinInfoList.forEach(joinInfo -> {
                volunteersList.add(volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>()
                        .eq("VOLUNTEERS_ID",joinInfo.getFkJoinVolunteer())));
            });
        }
        activityInfo.setVolunteersInfoList(volunteersList);
        return activityInfo;
    }

    @Override
    public Integer publicActivity(ActivityInfo activityInfo) {
        //发布活动，将活动信息插入活动表
        //判断活动图片是否为空，为空则设置默认值
        if (StrUtil.isEmpty(activityInfo.getActivityBanner())) {
            if (StrUtil.equals(activityInfo.getActivityClassic(),"志愿防疫")) {
                activityInfo.setActivityBanner("https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/defaultBanner1.png");
            } else {
                activityInfo.setActivityBanner("https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/defaultBanner.jpg");
            }
        }
        activityInfo.setActivityPublic(DateUtil.now());
        int insert = activityInfoMapper.insert(activityInfo);
        //插入活动表成功，则添加一条发布志愿活动的申请信息，以便管理员审核
        if (insert > 0) {
            ApplyInfo applyInfo = new ApplyInfo();
            applyInfo.setApplyTitle("发布志愿活动");
            applyInfo.setApplyDescription("发布新的志愿活动，请审核");
            //设置申请类型为发布志愿活动
            applyInfo.setApplyType(2);
            //设置申请人为发布活动的志愿单位
            applyInfo.setFkApplicantId(activityInfo.getFkActivityPublisher());
            applyInfo.setFkApplicantType(2);
            //设置审核该申请信息的人为管理员
            applyInfo.setFkAuditorId(1);
            applyInfo.setFkAuditorType(3);
            //设置申请发布的志愿活动
            applyInfo.setFkAppliedActivity(activityInfo.getActivityId());
            //调用发起消息通知的方法
            messageInfoService.insertMessage("志愿活动发布审核通知","",activityInfo.getFkActivityPublisher(),2,1,3,activityInfo.getActivityId());
            return applyInfoService.insertApply(applyInfo);
        }
        return -1;
    }

    @Override
    public Map<Object, Object> getActivityRecord(Integer userId, Integer userType, Integer currentPage) {
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        if (userType == 0) {
            //根据志愿者查询其申请参加活动审核通过的活动记录，且这些活动状态必须是可见的
            List<JoinInfo> joinInfos = joinInfoMapper.selectJoinByActivityShowPage(userId,(currentPage - 1) * 10);
            if (joinInfos.size() > 0) {
                joinInfos.forEach(joinInfo -> {
                    ActivityInfo activityInfo = activityInfoMapper.selectOne(new QueryWrapper<ActivityInfo>().eq("ACTIVITY_ID",joinInfo.getFkJoinActivity()));
                    if (activityInfo != null) {
                        activityInfo.setPublisherUnitName(unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().eq("UNIT_ID",activityInfo.getFkActivityPublisher())).getUnitName());
                        //遍历join列表，查询到该条记录对应的活动信息
                        joinInfo.setActivityInfo(activityInfo);
                    }
                });
            }
            map.put("activityRecord",joinInfos);
            map.put("total",joinInfos.size());
        } else if (userType == 2) {
            Page<ActivityInfo> page = new Page<>(currentPage, 10);
            QueryWrapper<ActivityInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("FK_ACTIVITY_PUBLISHER",userId);
            wrapper.eq("ACTIVITY_SHOW",1);
            Page<ActivityInfo> activityInfoPage = activityInfoMapper.selectPage(page, wrapper);
            List<ActivityInfo> records = activityInfoPage.getRecords();
            if (records.size() > 0) {
                records.forEach(activityInfo -> {
                    activityInfo.setActivityApplied(joinInfoMapper.selectCount(new QueryWrapper<JoinInfo>().eq("FK_JOIN_ACTIVITY",activityInfo.getActivityId())));
                    activityInfo.setStaredCount(starInfoMapper.selectCount(new QueryWrapper<StarInfo>().eq("FK_STAR_ACTIVITY",activityInfo.getActivityId())));
                });
            }
            map.put("activityRecord",records);
            map.put("total",activityInfoPage.getTotal());
        } else if (userType == 3) {
            Page<ActivityInfo> page = new Page<>(currentPage, 15);
            Page<ActivityInfo> activityInfoPage = activityInfoMapper.selectPage(page, new QueryWrapper<ActivityInfo>().orderByDesc("ACTIVITY_PUBLIC"));
            List<ActivityInfo> records = activityInfoPage.getRecords();
            if (records.size() > 0) {
                records.forEach(activityInfo -> {
                    activityInfo.setPublisherUnitName(unitInfoMapper.selectOne(new QueryWrapper<UnitInfo>().eq("UNIT_ID",activityInfo.getFkActivityPublisher())).getUnitName());
                    activityInfo.setActivityApplied(joinInfoMapper.selectCount(new QueryWrapper<JoinInfo>().eq("FK_JOIN_ACTIVITY",activityInfo.getActivityId())));
                    activityInfo.setStaredCount(starInfoMapper.selectCount(new QueryWrapper<StarInfo>().eq("FK_STAR_ACTIVITY",activityInfo.getActivityId())));
                    if (DateUtil.parseDate(DateUtil.now()).after(DateUtil.parseDate(activityInfo.getActivityCutoff()))) {
                        activityInfo.setExpired(true);
                    }
                });
            }
            map.put("activityRecord",records);
            map.put("total",activityInfoPage.getTotal());
        }
        return map;
    }

    @Override
    public boolean changeActivityShow(Integer activityId, Integer changeShow) {
        UpdateWrapper<ActivityInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("ACTIVITY_ID",activityId)
                .set("ACTIVITY_SHOW",changeShow);
        return activityInfoMapper.update(null,wrapper) > 0;
    }

    @Override
    public boolean deleteActivityRecord(List<Integer> activityIds) {
        //志愿单位删除活动记录
        int deleteActivity = activityInfoMapper.deleteBatchIds(activityIds);
        if (deleteActivity > 0) {
            //将这些活动记录对应的申请信息、收藏信息、已参加活动的信息删除
            activityIds.forEach(activityId -> {
                applyInfoMapper.delete(new QueryWrapper<ApplyInfo>().eq("FK_APPLIED_ACTIVITY",activityId));
                starInfoMapper.delete(new QueryWrapper<StarInfo>().eq("FK_STAR_ACTIVITY",activityId));
                joinInfoMapper.delete(new QueryWrapper<JoinInfo>().eq("FK_JOIN_ACTIVITY",activityId));
            });
            return true;
        }
        return false;
    }
}




