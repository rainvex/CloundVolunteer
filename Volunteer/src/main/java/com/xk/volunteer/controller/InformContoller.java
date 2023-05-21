package com.xk.volunteer.controller;

import com.xk.volunteer.entity.InformInfo;
import com.xk.volunteer.service.InformInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Rainvex
 * @Date 2022/12/28
 * @Name InformContoller
 */
@RestController
@RequestMapping("/inform")
public class InformContoller {
    @Autowired
    private InformInfoService informInfoService;

    @PostMapping("/getAllInform")
    public Response getAllInform(@RequestParam(value = "userType",required = false) Integer userType,
                                 @RequestParam("currentPage") Integer currentPage) {
        List<InformInfo> informInfos = informInfoService.selectAllInform(userType, currentPage);
        if (informInfos != null) {
            return Response.SUCCEED().carry("informList",informInfos);
        } else {
            return Response.DEFEAT();
        }
    }

    @GetMapping("/getInformHighView")
    public Response getInformHighView() {
        return Response.SUCCEED().carry("highViewInform",informInfoService.selectInformHighView());
    }

    @PostMapping("/getInform")
    public Response getInform(@RequestParam("informId") Integer informId) {
        return Response.SUCCEED().carry("informInfo",informInfoService.selectInformInfo(informId));
    }

    @PostMapping("/publicInform")
    public Response publicInform(@RequestBody InformInfo informInfo) {
        String inform = informInfoService.publicInform(informInfo);
        if (inform.contains("成功")) {
            return Response.SUCCEED(inform);
        }
        return Response.DEFEAT(500, inform);
    }

    @PostMapping("/changeInformStatus")
    public Response changeInformStatus(@RequestParam("informId") Integer informId,
                                       @RequestParam("changedStatus") Integer changedStatus) {
        boolean changeInformStatus = informInfoService.changeInformStatus(informId, changedStatus);
        if (changeInformStatus) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT();
    }

    @PostMapping("/deleteInform")
    public Response deleteInform(@RequestParam("informIds") List<Integer> informIds) {
        boolean deleteInform = informInfoService.deleteInform(informIds);
        if (deleteInform) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT();
    }
}
