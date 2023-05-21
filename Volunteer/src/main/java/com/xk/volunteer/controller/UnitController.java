package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.UnitInfo;
import com.xk.volunteer.entity.VolunteersInfo;
import com.xk.volunteer.service.UnitInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name UnitController
 */
@RestController
@RequestMapping("/unit")
public class UnitController {

    @Autowired
    private UnitInfoService unitInfoService;

    @PostMapping("/queryByCondition")
    public Response getActivityByCondition(@RequestParam("province") String province,
                                           @RequestParam("city") String city,
                                           @RequestParam("classic") String classic,
                                           @RequestParam("currentPage") Integer currentPage){
        Page<UnitInfo> page = new Page<>(currentPage, 12);
        Page<UnitInfo> unitByCondition = unitInfoService.getUnitByCondition(province, city, classic, page);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("unitList",unitByCondition.getRecords());
        map.put("total",unitByCondition.getTotal());
        return Response.SUCCEED().carry("unitData",map);
    }
}
