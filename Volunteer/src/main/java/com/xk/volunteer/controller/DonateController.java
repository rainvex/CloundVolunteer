package com.xk.volunteer.controller;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.Alipay;
import com.xk.volunteer.entity.DonateInfo;
import com.xk.volunteer.service.DonateInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name DonateController
 */
@RestController
@RequestMapping("/donate")
public class DonateController {

    @Autowired
    private DonateInfoService donateInfoService;

    @PostMapping("/initDonate")
    public Response initDonate(@RequestBody Alipay alipay) {
        String payPage = donateInfoService.initAliPayPage(alipay);
        if (!StrUtil.equals(payPage,"")) {
            return Response.SUCCEED().carry("alipayBody",payPage);
        }
        return Response.DEFEAT(500,"系统错误");
    }

    @PostMapping("/donateCallback")
    public String donateSuccess(@RequestParam Map<String, String> params) {
        return donateInfoService.donateCallback(params);
    }

    @PostMapping("/getDonateRecord")
    public Response getDonateRecord(@RequestParam("userId") Integer userId,
                                    @RequestParam("userType") Integer userType,
                                    @RequestParam("currentPage") Integer currentPage){
        Page<DonateInfo> page = new Page<>(currentPage,15);
        return Response.SUCCEED().carry("donateRecord",donateInfoService.getDonateRecord(userId, userType, page));
    }

    @PostMapping("/deleteDonate")
    public Response deleteSingle(@RequestParam("donateIds") List<Integer> donateIds) {
        if (donateInfoService.deleteDonate(donateIds)) return Response.SUCCEED();
        return Response.DEFEAT();
    }
}
