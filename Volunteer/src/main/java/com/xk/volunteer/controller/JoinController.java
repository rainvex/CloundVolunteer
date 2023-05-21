package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xk.volunteer.entity.JoinInfo;
import com.xk.volunteer.entity.VolunteersInfo;
import com.xk.volunteer.service.JoinInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Rainvex
 * @Date 2022/7/22
 * @Name JoinController
 */
@RestController
@RequestMapping("/join")
public class JoinController {
    @Autowired
    private JoinInfoService joinInfoService;

    @PostMapping("/getVolunteersByActivityJoin")
    public Response getVolunteersByActivityJoin(@RequestParam("activityId") Integer activityId) {
        return Response.SUCCEED().carry("joinList",joinInfoService.getVolunteersByActivityJoin(activityId));
    }

    @PostMapping("/completeJoin")
    public Response completeJoin(@RequestParam("activityId") Integer activityId,
                                 @RequestParam("userId") Integer userId) {
        if (joinInfoService.completeActivity(activityId, userId)) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT();
    }

    @PostMapping("/deleteJoin")
    public Response deleteActivityRecord(@RequestParam("joinIds") List<Integer> joinIds) {
        if (joinInfoService.deleteJoins(joinIds)) return Response.SUCCEED();
        return Response.DEFEAT();
    }
}
