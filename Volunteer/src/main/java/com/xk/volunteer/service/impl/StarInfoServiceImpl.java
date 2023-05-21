package com.xk.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.StarInfo;
import com.xk.volunteer.mapper.ActivityInfoMapper;
import com.xk.volunteer.service.ActivityInfoService;
import com.xk.volunteer.service.StarInfoService;
import com.xk.volunteer.mapper.StarInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Administrator
* @description 针对表【t_star_info(志愿活动被收藏的信息表)】的数据库操作Service实现
* @createDate 2022-07-22 16:26:10
*/
@Service
@Transactional
public class StarInfoServiceImpl extends ServiceImpl<StarInfoMapper, StarInfo>
    implements StarInfoService{

    @Autowired
    private StarInfoMapper starInfoMapper;
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;

    @Override
    public Boolean getIsStared(Integer userId, Integer activeId) {
        QueryWrapper<StarInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("FK_STAR_ACTIVITY",activeId);
        wrapper.eq("FK_STAR_VOLUNTEER",userId);
        return starInfoMapper.exists(wrapper);
    }

    @Override
    public ActivityInfo starActivity(Integer userId, Integer activeId) {
        StarInfo starInfo = new StarInfo();
        starInfo.setFkStarVolunteer(userId);
        starInfo.setFkStarActivity(activeId);
        int insertStar = starInfoMapper.insert(starInfo);
        if (insertStar > 0) {
            //如果收藏成功，再次根据活动id查询出该活动的信息，对页面进行更新
            return activityInfoService.getActivityById(activeId);
        }
        return null;
    }

    @Override
    public Map<Object, Object> getAllStarByVolunteer(Integer userId, Integer currentPage) {
        List<StarInfo> starList = starInfoMapper.selectStarByActivityShow(userId, (currentPage - 1) * 12);
        if (starList.size() > 0) {
            //查询该用户所有的活动收藏记录对应的活动信息
            starList.forEach(starInfo -> {
                starInfo.setActivityInfo(activityInfoMapper.selectById(starInfo.getFkStarActivity()));
            });
        }
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("starList",starList);
        map.put("total",starList.size());
        return map;
    }

    @Override
    public Integer cancelStar(Integer starId) {
        return starInfoMapper.delete(new QueryWrapper<StarInfo>().eq("STAR_ID", starId));
    }
}




