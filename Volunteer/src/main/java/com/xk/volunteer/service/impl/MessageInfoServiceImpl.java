package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.entity.MessageInfo;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Administrator
* @description 针对表【t_message_info(用户消息信息表)】的数据库操作Service实现
* @createDate 2022-08-18 20:57:08
*/
@Service
@Transactional
public class MessageInfoServiceImpl extends ServiceImpl<MessageInfoMapper, MessageInfo>
    implements MessageInfoService{

    @Autowired
    private TeamInfoMapper teamInfoMapper;
    @Autowired
    private UnitInfoMapper unitInfoMapper;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private AdminInfoMapper adminInfoMapper;
    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private MessageInfoMapper messageInfoMapper;

    @Override
    public Integer insertMessage(String title, String content, Integer sender, Integer senderType, Integer accepter, Integer accepterType, Integer foreignId) {
        String messageContent = content;
        MessageInfo messageInfo = new MessageInfo();
        if (StrUtil.equals(title,"志愿者团队注册审核通知")) {
            messageContent = "有新的志愿者团队用户申请注册，请尽快前往审核，志愿者团队名为" + teamInfoMapper.selectById(sender).getTeamName();
            messageInfo.setMessageType(0);
        } else if (StrUtil.equals(title,"志愿单位注册审核通知")) {
            messageContent = "有新的志愿单位用户申请注册，请尽快前往审核，志愿单位名为" + unitInfoMapper.selectById(sender).getUnitName();
            messageInfo.setMessageType(0);
        } else if (StrUtil.equals(title,"志愿活动发布审核通知")) {
            messageContent = "有新的志愿活动申请发布，请尽快前往审核，志愿活动名为" + activityInfoMapper.selectById(foreignId).getActivityTitle();
            messageInfo.setMessageType(0);
        } else if (StrUtil.equals(title,"申请参加活动审核通知")) {
            messageContent = "有志愿者正在申请参加志愿活动，请尽快前往审核，申请参加的志愿活动为" + activityInfoMapper.selectById(foreignId).getActivityTitle();
            messageInfo.setMessageType(0);
        } else if (StrUtil.equals(title,"申请加入团队审核通知")) {
            messageContent = "有志愿者正在申请加入我们团队，请尽快前往审核，5天后将自动审核不通过，志愿者用户名为" + volunteersInfoMapper.selectById(sender).getVolunteersUsername();
            messageInfo.setMessageType(0);
        } else if (StrUtil.equals(title,"申请参加活动审核结果通知")) {
            if (StrUtil.equals(messageContent,"审核通过")){
                messageContent = "恭喜您，申请参加的志愿活动" + activityInfoMapper.selectById(foreignId).getActivityTitle() + "已成功通过审核，请前往您的个人信息 > 我的活动进行查看，希望您能准时并按要求参与本次志愿活动，合作愉快！";
            } else {
                messageContent = "很遗憾，您暂且不符合我们本次志愿活动" + activityInfoMapper.selectById(foreignId).getActivityTitle() + "的参与要求，还望您再接再厉，下次有机会再一起合作。";
            }
            messageInfo.setMessageType(1);
        } else if (StrUtil.equals(title,"申请加入团队审核结果通知")) {
            if (StrUtil.equals(messageContent,"审核通过")) {
                messageContent = "恭喜您，已经成功申请加入我们团队，可前往您的个人信息 > 团队信息查看我们团队的详情，希望一起努力，为志愿服务事业献出自己的一份绵薄之力。";
            } else {
                messageContent = "很遗憾，我们已经收到您想加入我们团队的申请，并感受到了您的强烈意愿，但您暂不符合我们团队的要求，还望继续努力。";
            }
            messageInfo.setMessageType(1);
        } else if (StrUtil.equals(title,"申请发布活动审核结果通知")) {
            if (StrUtil.equals(messageContent,"审核通过")) {
                messageContent = "恭喜您，申请发布的志愿活动" + activityInfoMapper.selectById(foreignId).getActivityTitle() + "已经成功通过审核，请前往您的个人信息 > 我的活动进行查看！";
            } else {
                messageContent = "抱歉，您申请发布的志愿活动" + activityInfoMapper.selectById(foreignId).getActivityTitle() + "不符合要求，请作修改后重新申请发布";
            }
            messageInfo.setMessageType(1);
        } else {
            messageInfo.setMessageType(2);
        }
        messageInfo.setMessageTitle(title);
        messageInfo.setMessageContent(messageContent);
        messageInfo.setMessageTime(DateUtil.now());
        messageInfo.setFkMessageSender(sender);
        messageInfo.setFkMessageSendertype(senderType);
        messageInfo.setFkMessageAccepter(accepter);
        messageInfo.setFkMessageAcceptertype(accepterType);
        return messageInfoMapper.insert(messageInfo);
    }

    @Override
    public List<MessageInfo> getMessageByUser(Integer userId, Integer userType, Integer currentPage) {
        QueryWrapper<MessageInfo> wrapper = new QueryWrapper<>();
        Page<MessageInfo> page = new Page<>(currentPage,15);
        wrapper.select("MESSAGE_ID,MESSAGE_TITLE,MESSAGE_CONTENT,MESSAGE_TYPE,MESSAGE_TIME,MESSAGE_STATUS")
                .eq("FK_MESSAGE_ACCEPTER",userId)
                .eq("FK_MESSAGE_ACCEPTERTYPE",userType)
                //接收者为null表示是系统消息
                .or(i -> i.isNull("FK_MESSAGE_ACCEPTER")
                        .isNull("FK_MESSAGE_ACCEPTERTYPE"))
                .orderByDesc("MESSAGE_TIME");
        return messageInfoMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public Long getMessageNumByStatusAndUser(Integer userId, Integer userType) {
        QueryWrapper<MessageInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("MESSAGE_STATUS",0)
                .and(i -> i.eq("FK_MESSAGE_ACCEPTER",userId)
                        .eq("FK_MESSAGE_ACCEPTERTYPE",userType)
                        .or(j -> j.isNull("FK_MESSAGE_ACCEPTER")
                                .isNull("FK_MESSAGE_ACCEPTERTYPE")));
        return messageInfoMapper.selectCount(wrapper);
    }

    @Override
    public Map<Object,Object> getMessageById(Integer messageId) {
        //先将该消息的状态设为已读
        messageInfoMapper.update(null, new UpdateWrapper<MessageInfo>().eq("MESSAGE_ID", messageId).set("MESSAGE_STATUS", 1));
        MessageInfo message = messageInfoMapper.selectOne(new QueryWrapper<MessageInfo>().eq("MESSAGE_ID", messageId));
        if (message != null) {
            Integer messageType = message.getMessageType();
            //如果消息类型是审核结果通知和系统消息，则设置该消息的发送者
            if (messageType == 1 || messageType == 2) {
                Integer fkMessageSender = message.getFkMessageSender();
                Integer fkMessageSendertype = message.getFkMessageSendertype();
                String senderName = "";
                if (fkMessageSendertype == 1) {
                    senderName = teamInfoMapper.selectById(fkMessageSender).getTeamName();
                } else if (fkMessageSendertype == 2) {
                    senderName = unitInfoMapper.selectById(fkMessageSender).getUnitName();
                } else if (fkMessageSendertype == 3) {
                    senderName = adminInfoMapper.selectById(fkMessageSender).getAdminName();
                }
                message.setMessageSender(senderName);
            }
            //判断该用户查看该条消息后还是否有其他未读消息
            Long messageCount = getMessageNumByStatusAndUser(message.getFkMessageAccepter(), message.getFkMessageAcceptertype());
            ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
            map.put("messageInfo",message);
            map.put("messageCount",messageCount);
            return map;
        }
        return null;
    }

    @Override
    public boolean deleteMessage(List<Integer> messageIds) {
        return messageInfoMapper.deleteBatchIds(messageIds) > 0;
    }
}




