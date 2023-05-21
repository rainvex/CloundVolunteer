package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName t_team_info
 */
@TableName(value ="t_team_info")
@Data
public class TeamInfo implements Serializable {
    /**
     * 志愿者团队id（注册获取）
     */
    @TableId(type = IdType.AUTO)
    private Integer teamId;

    /**
     * 志愿者团队名（注册获取）
     */
    private String teamName;

    /**
     * 志愿者团队邮箱（注册获取）
     */
    private String teamEmail;

    /**
     * 志愿者团队固定电话（注册获取）
     */
    private String teamPhone;

    /**
     * 志愿者团队密码（注册获取）
     */
    private String teamPassword;

    /**
     * 志愿者团队地址（注册获取）
     */
    private String teamAddress;

    /**
     * 志愿者团队简介
     */
    private String teamSimple;

    /**
     * 志愿者团队服务对象（注册获取）
     */
    private String teamObject;

    /**
     * 志愿者团队头像（注册获取）
     */
    private String teamAvatar;

    /**
     * 志愿者团队证明（图片地址）（注册获取）
     */
    private String teamProve;

    /**
     * 志愿者团队类型（社区？高校？）（注册获取）
     */
    private String teamClassic;

    /**
     * 志愿者团队注册时间（注册时后端赋予）
     */
    private String teamRegister;

    /**
     * 志愿者团队负责人姓名（注册获取）
     */
    private String teamHeadName;

    /**
     * 志愿者团队负责人邮箱（注册获取）
     */
    private String teamHeadEmail;

    /**
     * 志愿者团队负责人手机（注册获取）
     */
    private String teamHeadPhone;

    /**
     * 志愿者团队负责人证件号（注册获取）
     */
    private String teamHeadIdentity;

    /**
     * 志愿者团队负责人证件照（图片地址，注册获取）
     */
    private String teamHeadPicture;

    /**
     * 志愿者团队志愿总时长
     */
    private Integer teamHours;

    /**
     * 志愿者团队成员参与的志愿活动总数量
     */
    private Integer teamActivecount;

    /**
     * 志愿者团队志愿者数量
     */
    private Integer teamCount;

    /**
     * 用户类型标识
     */
    private Integer teamType;

    /**
     * 管理员决定志愿者团队账号是否启用（0-不启用 1-启用）
     */
    private Integer teamStatus;

    @TableField(exist = false)
    private List<VolunteersInfo> volunteersInfoList;

    @TableField(exist = false)
    private List<MessageInfo> messageInfoList;

    @TableField(exist = false)
    private List<DonateInfo> donateInfoList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}