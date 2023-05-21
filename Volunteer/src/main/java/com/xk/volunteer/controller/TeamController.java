package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.TeamInfo;
import com.xk.volunteer.entity.VolunteersInfo;
import com.xk.volunteer.service.TeamInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name TeamController
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamInfoService teamInfoService;

    @PostMapping("/queryByCondition")
    public Response getTeamByCondition(@RequestParam("province") String province,
                                           @RequestParam("city") String city,
                                           @RequestParam("classic") String classic,
                                           @RequestParam("serviceObj") String serviceObj,
                                           @RequestParam("currentPage") Integer currentPage){
        Page<TeamInfo> page = new Page<>(currentPage, 12);
        Page<TeamInfo> teamByCondition = teamInfoService.getTeamByCondition(province, city, classic, serviceObj, page);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("teamList",teamByCondition.getRecords());
        map.put("total",teamByCondition.getTotal());
        return Response.SUCCEED().carry("teamData",map);
    }

    @PostMapping("/getTeamInfo")
    public Response getTeamInfo(@RequestParam("userId") Integer userId,
                                @RequestParam("userType") Integer userType,
                                @RequestParam("currentPage") Integer currentPage) {
        Map<Object, Object> teamInfo = teamInfoService.getTeamInfo(userId, userType, currentPage);
        if (teamInfo == null) {
            return Response.DEFEAT(440,"该用户还未加入志愿者团队");
        }
        return Response.SUCCEED().carry("teamData",teamInfo);
    }

    @PostMapping("/memberKickOrExit")
    public Response memberKickOrExit(@RequestParam("volunteerIds") List<Integer> volunteerIds,
                               @RequestParam("teamId") Integer teamId) {
        if (teamInfoService.memberKickOrExit(volunteerIds,teamId)) return Response.SUCCEED();
        return Response.DEFEAT();
    }
}
