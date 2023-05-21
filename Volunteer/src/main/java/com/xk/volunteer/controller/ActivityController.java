package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.service.ActivityInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name ActivityController
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityInfoService activityInfoService;

    @PostMapping("/queryByCondition")
    public Response getActivityByCondition(@RequestParam("province") String province,
                         @RequestParam("city") String city,
                         @RequestParam("classic") String classic,
                         @RequestParam("currentPage") Integer currentPage){
        Page<ActivityInfo> page = new Page<>(currentPage, 10);
        return Response.SUCCEED().carry("activityData",activityInfoService.getActivityByCondition(province, city, classic, page));
    }

    @PostMapping("/getActivityById")
    public Response getActivityById(@RequestParam("activeId") Integer activeId) {
        ActivityInfo activityById = activityInfoService.getActivityById(activeId);
        if (activityById != null) return Response.SUCCEED().carry("activity",activityById);
        return Response.DEFEAT(430,"该活动不存在");
    }

    @PostMapping("/public")
    public Response publicActivity(@RequestBody ActivityInfo activityInfo) {
        Integer publicActivity = activityInfoService.publicActivity(activityInfo);
        if (publicActivity > 0) return Response.SUCCEED();
        return Response.DEFEAT(500,"系统出错，发布失败");
    }

    @PostMapping("/getActivityRecord")
    public Response getActivityRecord(@RequestParam("userId") Integer userId,
                                      @RequestParam("userType") Integer userType,
                                      @RequestParam("currentPage") Integer currentPage) {
        Map<Object, Object> map = activityInfoService.getActivityRecord(userId, userType, currentPage);
        if (map.isEmpty()) {
            return Response.DEFEAT();
        }
        return Response.SUCCEED().carry("activityRecord",map);
    }

    @PostMapping("/changeActivityShow")
    public Response changeActivityShow(@RequestParam("activityId") Integer activityId,
                                       @RequestParam("changeShow") Integer changeShow) {
        if (activityInfoService.changeActivityShow(activityId, changeShow)) return Response.SUCCEED();
        else return Response.DEFEAT();
    }

    @PostMapping("/deleteActivity")
    public Response deleteActivityRecord(@RequestParam("activityIds") List<Integer> activityIds) {
        boolean deleteActivityRecord = activityInfoService.deleteActivityRecord(activityIds);
        if (deleteActivityRecord) return Response.SUCCEED();
        return Response.DEFEAT();
    }
}
