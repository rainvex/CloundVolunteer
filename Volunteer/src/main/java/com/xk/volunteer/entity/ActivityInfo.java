package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 志愿活动信息表
 * @TableName t_activity_info
 */
@TableName(value ="t_activity_info")
@Data
public class ActivityInfo implements Serializable {
    /**
     * 志愿活动id
     */
    @TableId(type = IdType.AUTO)
    private Integer activityId;

    /**
     * 志愿活动标题
     */
    private String activityTitle;

    /**
     * 志愿活动分类
     */
    private String activityClassic;

    /**
     * 志愿活动发布时间
     */
    private String activityPublic;

    /**
     * 志愿活动banner图地址
     */
    private String activityBanner;

    /**
     * 志愿活动活动时间（开始时间 - 结束时间）
     */
    private String activityContinue;

    /**
     * 志愿活动地点
     */
    private String activityPlace;

    /**
     * 志愿活动内容简介
     */
    private String activitySimple;

    /**
     * 志愿活动内容
     */
    private String activityContent;

    /**
     * 志愿活动报名要求
     */
    private String activityRequire;

    /**
     * 志愿活动负责人姓名
     */
    private String activityContact;

    /**
     * 志愿活动负责人电话
     */
    private String activityPhone;

    /**
     * 志愿活动报名截止时间
     */
    private String activityCutoff;

    /**
     * 志愿活动参与人数限制
     */
    private Integer activityLimit;

    /**
     * 管理员决定志愿活动是否显示（0-不显示 1-显示）
     */
    private Integer activityShow;

    /**
     * 志愿活动发布单位
     */
    private Integer fkActivityPublisher;

    @TableField(exist = false)
    private String publisherUnitName;

    @TableField(exist = false)
    private List<VolunteersInfo> volunteersInfoList;

    //志愿活动已招募的人数
    @TableField(exist = false)
    private Long activityApplied;

    //收藏该活动的志愿者数量
    @TableField(exist = false)
    private Long staredCount;

    //用于判断当前时间是否已经超过报名截止时间
    @TableField(exist = false)
    private boolean isExpired = false;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}