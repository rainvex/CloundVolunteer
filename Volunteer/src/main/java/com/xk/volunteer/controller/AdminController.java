package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.InformInfo;
import com.xk.volunteer.service.AdminInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name AdminController
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminInfoService adminInfoService;

    @GetMapping("/home")
    public Response home() {
        return Response.SUCCEED().carry("homeData",adminInfoService.home());
    }

    @PostMapping("/getUserList")
    public Response getUserList(@RequestParam("userType") Integer userType,
                                @RequestParam("currentPage") Integer currentPage) {
        List userList = adminInfoService.getUserList(userType, currentPage);
        if (userList != null) return Response.SUCCEED().carry("userList",userList);
        else return Response.DEFEAT(500,"系统出错");
    }
}
