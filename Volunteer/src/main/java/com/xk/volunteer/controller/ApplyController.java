package com.xk.volunteer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ApplyInfo;
import com.xk.volunteer.service.ApplyInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name ApplyController
 */
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplyInfoService applyInfoService;

    //该接口用于志愿者申请参加志愿活动和申请加入团队
    //其调用的service方法还用于志愿活动发布的审核和志愿者团队注册及志愿单位注册的审核
    @PostMapping("/insertApply")
    public Response getIsApplied(@RequestBody ApplyInfo applyInfo) {
        Integer apply = applyInfoService.insertApply(applyInfo);
        if (apply > 0) return Response.SUCCEED();
        return Response.DEFEAT(500,"系统错误，申请失败");
    }

    @PostMapping("/activityApply")
    public Response getActivityApply(@RequestParam("userId") Integer userId,
                                     @RequestParam("userType") Integer userType,
                                     @RequestParam("currentPage") Integer currentPage) {
        return Response.SUCCEED().carry("activityApplyData",applyInfoService.getActivityApply(userId,userType,currentPage));
    }

    @PostMapping("/getIsApplied")
    public Response getIsApplied(@RequestParam("userId") Integer userId,
                                 @RequestParam("activeId") Integer activeId) {
        return Response.SUCCEED().carry("isApplied",applyInfoService.getIsApplied(userId,activeId));
    }

    @PostMapping("/cancelApplyActivity")
    public Response cancelApply(@RequestParam("userId") Integer userId,
                                @RequestParam("userType") Integer userType,
                                @RequestParam("activeId") Integer activeId) {
        Integer cancelApply = applyInfoService.cancelApplyActivity(userId,userType,activeId);
        if (cancelApply > 0) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT();
    }

    @PostMapping("/getAuditInfo")
    public Response getAuditInfo(@RequestParam("userId") Integer userId,
                                 @RequestParam("userType") Integer userType,
                                 @RequestParam(value = "applyType",required = false) Integer applyType,
                                 @RequestParam("currentPage") Integer currentPage) {
        Page<ApplyInfo> page = new Page<>(currentPage, 15);
        return Response.SUCCEED().carry("auditData",applyInfoService.getAuditInfo(userId,userType,applyType,page));
    }

    @GetMapping("/queryApplyById/{applyId}")
    public Response queryApplyById(@PathVariable("applyId") Integer applyId) {
        //用于接收志愿者团队和志愿单位查看申请详细信息的请求
        ApplyInfo applyInfo = applyInfoService.queryApplyById(applyId);
        if (applyInfo != null) {
            return Response.SUCCEED().carry("applyById",applyInfo);
        }
        return Response.DEFEAT();
    }

    @PostMapping("/deleteApply")
    public Response deleteApply(@RequestParam("applyIds") List<Integer> applyIds){
        boolean deleteApply = applyInfoService.deleteApply(applyIds);
        if (deleteApply) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT();
    }

    @PostMapping("/auditApply")
    public Response auditApply(@RequestParam("applyId") Integer applyId,
                               @RequestParam("auditCommand") String auditCommand) {
        boolean auditApply = applyInfoService.auditApply(applyId, auditCommand);
        if (auditApply) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT();
    }

}
