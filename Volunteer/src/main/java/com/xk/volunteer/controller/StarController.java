package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.StarInfo;
import com.xk.volunteer.service.StarInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Rainvex
 * @Date 2022/7/22
 * @Name StarController
 */
@RestController
@RequestMapping("/star")
public class StarController {

    @Autowired
    private StarInfoService starInfoService;

    @PostMapping("/getIsStared")
    public Response getIsStared(@RequestParam("userId") Integer userId,@RequestParam("activeId") Integer activeId) {
        return Response.SUCCEED().carry("isStared",starInfoService.getIsStared(userId, activeId));
    }

    @PostMapping("/starActivity")
    public Response starActivity(@RequestParam("userId") Integer userId,
                                 @RequestParam("activeId") Integer activeId) {
        ActivityInfo activityInfo = starInfoService.starActivity(userId, activeId);
        if (activityInfo != null) {
            return Response.SUCCEED().carry("newActivity",activityInfo);
        }
        return Response.DEFEAT(500,"系统错误，收藏失败");
    }

    @PostMapping("/getAllStarByVolunteer")
    public Response getAllStarByVolunteer(@RequestParam("userId") Integer userId,
                                          @RequestParam("currentPage") Integer currentPage) {
        return Response.SUCCEED().carry("allStarByVolunteer",starInfoService.getAllStarByVolunteer(userId,currentPage));
    }

    @GetMapping("/cancelStar/{starId}")
    public Response cancelStar(@PathVariable("starId") Integer starId) {
        Integer integer = starInfoService.cancelStar(starId);
        if (integer > 0) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT(500,"系统错误，取消收藏失败");
    }
}
