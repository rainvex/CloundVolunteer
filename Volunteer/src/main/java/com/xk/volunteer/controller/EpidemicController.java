package com.xk.volunteer.controller;

import cn.hutool.http.HttpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Rainvex
 * @Date 2022/7/25
 * @Name EpidemicController
 */
@RestController
@RequestMapping("/echarts")
public class EpidemicController {

    @GetMapping("/epidemic")
    public String epidemic() {
        return HttpUtil.get("https://c.m.163.com/ug/api/wuhan/app/data/list-total?t=" + System.currentTimeMillis());
    }
}
