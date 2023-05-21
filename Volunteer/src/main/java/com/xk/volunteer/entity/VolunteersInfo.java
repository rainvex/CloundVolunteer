package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 志愿者信息表
 * @TableName t_volunteers_info
 */
@TableName(value ="t_volunteers_info")
@Data
public class VolunteersInfo implements Serializable {
    /**
     * 志愿者id（注册时获取）
     */
    @TableId(type = IdType.AUTO)
    private Integer volunteersId;

    /**
     * 志愿者用户名（注册时获取）
     */
    private String volunteersUsername;

    /**
     * 志愿者邮箱（注册时获取）
     */
    private String volunteersEmail;

    /**
     * 志愿者手机号（注册时获取）
     */
    private String volunteersPhone;

    /**
     * 志愿者密码（注册时获取）
     */
    private String volunteersPassword;

    /**
     * 志愿者政治面貌（注册时获取）
     */
    private String volunteersPolitical;

    /**
     * 志愿者从业情况（注册时获取）
     */
    private String volunteersProfessional;

    /**
     * 志愿者学历（注册时填写）
     */
    private String volunteersDegree;

    /**
     * 志愿者联系地址（注册时获取）
     */
    private String volunteersAddress;

    /**
     * 志愿者所属省份（注册时解析获得）
     */
    private String volunteersProvince;

    /**
     * 志愿者头像地址（注册时后端给予默认头像地址）
     */
    private String volunteersAvatar;

    /**
     * 注册时间（注册时后端赋予）
     */
    private String volunteersRegister;

    /**
     * 志愿者个人简介（自愿填写）
     */
    private String volunteersSimple;

    /**
     * 志愿者姓名（实名认证获取）
     */
    private String volunteersName;

    /**
     * 志愿者性别（实名认证获取）
     */
    private String volunteersGender;

    /**
     * 志愿者年龄（实名认证获取）
     */
    private String volunteersBirth;

    /**
     * 志愿者民族（实名认证获取）
     */
    private String volunteersNational;

    /**
     * 志愿者籍贯（实名认证获取）
     */
    private String volunteersNative;

    /**
     * 志愿者身份证号（实名认证获取）
     */
    private String volunteersCard;

    /**
     * 志愿时长（所有申请的志愿活动时长之和）
     */
    private Integer volunteersHours;

    /**
     * 志愿者参与的志愿活动数量
     */
    private Integer volunteersActivecount;

    /**
     * 违约次数（申请志愿活动违约次数）
     */
    private Integer volunteersBreak;

    /**
     * 用户类型标识（用于区别用户类型）
     */
    private Integer volunteersType;

    /*
     * 标识该志愿者是否正在申请一个志愿者团队
     */
    private Integer volunteersIsapplyteam;

    /**
     * 志愿者账号状态（由管理员决定是否启用该账号 1-启用 0-不启用）
     */
    private Integer volunteersStatus;

    /**
     * 志愿者所属志愿者团队（加入志愿者团队获取）
     */
    private Integer fkVolunteersTeam;

    @TableField(exist = false)
    private TeamInfo teamInfo;

    @TableField(exist = false)
    private List<ActivityInfo> activityInfoList;

    @TableField(exist = false)
    private List<MessageInfo> messageInfoList;

    @TableField(exist = false)
    private List<DonateInfo> donateInfoList;

    @TableField(exist = false)
    private List<JoinInfo> joinInfoList;

    @TableField(exist = false)
    private List<StarInfo> starInfoList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}