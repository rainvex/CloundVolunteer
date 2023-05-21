package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户消息信息表
 * @TableName t_message_info
 */
@TableName(value ="t_message_info")
@Data
public class MessageInfo implements Serializable {
    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Integer messageId;

    /**
     * 消息标题
     */
    private String messageTitle;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 消息状态（0-未读 1-已读）
     */
    private Integer messageStatus;

    /**
     * 消息发送时间
     */
    private String messageTime;

    /**
     * 消息类型（0-审核通知 1-审核结果通知 2-系统消息）
     */
    private Integer messageType;

    /**
     * 消息发送者
     */
    private Integer fkMessageSender;

    /**
     * 消息发送方类型
     */
    private Integer fkMessageSendertype;

    /**
     * 消息接收者
     */
    private Integer fkMessageAccepter;

    /**
     * 消息接收方类型
     */
    private Integer fkMessageAcceptertype;

    //消息发送方的用户名
    @TableField(exist = false)
    private String messageSender;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}