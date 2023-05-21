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
 * @TableName t_unit_info
 */
@TableName(value ="t_unit_info")
@Data
public class UnitInfo implements Serializable {
    /**
     * 志愿服务单位id（注册获取）
     */
    @TableId(type = IdType.AUTO)
    private Integer unitId;

    /**
     * 志愿服务单位名（注册获取）
     */
    private String unitName;

    /**
     * 志愿服务单位邮箱（注册获取）
     */
    private String unitEmail;

    /**
     * 志愿服务单位固定电话（注册获取）
     */
    private String unitPhone;

    /**
     * 志愿服务单位密码（注册获取）
     */
    private String unitPassword;

    /**
     * 志愿服务单位地址（注册获取）
     */
    private String unitAddress;

    /**
     * 志愿服务单位简介（注册获取）
     */
    private String unitSimple;

    /**
     * 志愿服务单位头像（由注册时后端给予默认头像地址）
     */
    private String unitAvatar;

    /**
     * 志愿服务单位证明（图片地址，注册获取）
     */
    private String unitProve;

    /**
     * 志愿服务单位类型（注册获取）
     */
    private String unitClassic;

    /**
     * 志愿服务单位负责人姓名（注册获取）
     */
    private String unitHeadName;

    /**
     * 志愿服务单位负责人手机（注册获取）
     */
    private String unitHeadPhone;

    /**
     * 志愿服务单位负责人邮箱（注册获取）
     */
    private String unitHeadEmail;

    /**
     * 志愿服务单位负责人证件号（注册获取）
     */
    private String unitHeadIdentity;

    /**
     * 志愿服务单位证件照（图片地址）（注册获取）
     */
    private String unitHeadPicture;

    /**
     * 志愿服务单位注册时间（注册获取）
     */
    private String unitRegister;

    /**
     * 志愿服务单位已发布的志愿活动数量
     */
    private Integer unitPubliccount;

    /**
     * 用户类型标识
     */
    private Integer unitType;

    /**
     * 管理员决定是否启用该账号（0-不启用 1-启用）
     */
    private Integer unitStatus;

    @TableField(exist = false)
    private List<ActivityInfo> activityInfoList;

    @TableField(exist = false)
    private List<MessageInfo> messageInfoList;

    @TableField(exist = false)
    private List<DonateInfo> donateInfoList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}