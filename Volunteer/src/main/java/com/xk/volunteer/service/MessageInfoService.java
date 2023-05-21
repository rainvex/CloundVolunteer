package com.xk.volunteer.service;

import com.xk.volunteer.entity.MessageInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_message_info(用户消息信息表)】的数据库操作Service
* @createDate 2022-08-18 20:57:08
*/
public interface MessageInfoService extends IService<MessageInfo> {
    Integer insertMessage(String title,String content,Integer sender,Integer senderType,Integer accepter,Integer accepterType,Integer foreignId);

    List<MessageInfo> getMessageByUser(Integer userId,Integer userType,Integer currentPage);

    Long getMessageNumByStatusAndUser(Integer userId,Integer userType);

    Map<Object,Object> getMessageById(Integer messageId);

    boolean deleteMessage(List<Integer> messageIds);
}
