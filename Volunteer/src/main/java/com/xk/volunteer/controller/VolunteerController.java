package com.xk.volunteer.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.VolunteersInfo;
import com.xk.volunteer.service.VolunteersInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name VolunteerController
 */
@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteersInfoService volunteersInfoService;

    @PostMapping("/queryByCondition")
    public Response getVolunteerByCondition(@RequestParam("province") String province,
                                            @RequestParam("city") String city,
                                            @RequestParam("currentPage") Integer currentPage){
        Page<VolunteersInfo> page = new Page<>(currentPage, 16);
        Page<VolunteersInfo> volunteerByCondition = volunteersInfoService.getVolunteerByCondition(province, city, page);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("volunteerList",volunteerByCondition.getRecords());
        map.put("total",volunteerByCondition.getTotal());
        return Response.SUCCEED().carry("volunteerData",map);
    }

    @PostMapping("/judgeTeam")
    public Response judgeTeam(@RequestParam("userId") Integer userId,
                              @RequestParam("detailId") Integer detailId,
                              @RequestParam("detailType") Integer detailType) {
        return Response.SUCCEED().carry("judgeTeam",volunteersInfoService.judgeTeam(userId,detailId,detailType));
    }

    @PostMapping("/auth")
    public Response auth(@RequestParam("file") MultipartFile file,
                         @RequestParam("userId") Integer userId) {
        String auth = volunteersInfoService.auth(file, userId);
        if (!StrUtil.equals(auth,"")) {
            return Response.SUCCEED().carry("newToken",auth);
        }
        return Response.DEFEAT(500,"识别发生错误");
    }
}
