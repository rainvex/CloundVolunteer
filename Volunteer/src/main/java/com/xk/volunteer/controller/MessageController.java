package com.xk.volunteer.controller;

import com.xk.volunteer.entity.MessageInfo;
import com.xk.volunteer.service.MessageInfoService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name MessageController
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageInfoService messageInfoService;

    //系统管理员发送系统公告消息调用
    @PostMapping("/publicMessage")
    public Response publicMessage(@RequestBody MessageInfo messageInfo) {
        Integer insertMessage = messageInfoService.insertMessage(messageInfo.getMessageTitle(), messageInfo.getMessageContent(), messageInfo.getFkMessageSender(), messageInfo.getFkMessageSendertype(), null, null, null);
        if (insertMessage > 0) return Response.SUCCEED();
        return Response.SUCCEED();
    }

    @PostMapping("/getMessageByUser")
    public Response getMessageByUser(@RequestParam("userId") Integer userId,
                                     @RequestParam("userType") Integer userType,
                                     @RequestParam("currentPage") Integer currentPage) {
        return Response.SUCCEED().carry("messageList",messageInfoService.getMessageByUser(userId, userType, currentPage));
    }

    //获取用户未读消息数量
    @PostMapping("/getMessageCount")
    public Response getMessageByUser(@RequestParam("userId") Integer userId,
                                     @RequestParam("userType") Integer userType) {
        return Response.SUCCEED().carry("messageCount",messageInfoService.getMessageNumByStatusAndUser(userId,userType));
    }

    @GetMapping("/getSingleMessage/{messageId}")
    public Response getMessageById(@PathVariable("messageId") Integer messageId) {
        Map<Object, Object> messageById = messageInfoService.getMessageById(messageId);
        if (messageById != null) return Response.SUCCEED().carry("messageById",messageById);
        return Response.DEFEAT();
    }

    @PostMapping("/deleteMessage")
    public Response getMessageByUser(@RequestParam("messageIds") List<Integer> messageIds) {
        if (messageInfoService.deleteMessage(messageIds)) return Response.SUCCEED();
        return Response.DEFEAT();
    }
}
