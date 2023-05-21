package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 申请信息表
 * @TableName t_apply_info
 */
@TableName(value ="t_apply_info")
@Data
public class ApplyInfo implements Serializable {
    /**
     * 申请信息id
     */
    @TableId(type = IdType.AUTO)
    private Integer applyId;

    /**
     * 申请信息标题
     */
    private String applyTitle;

    /**
     * 申请信息描述
     */
    private String applyDescription;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 申请类型（0-申请参加活动 1-申请加入团队 2-发布志愿活动 3-志愿者团队注册 4-志愿单位注册）
     */
    private Integer applyType;

    /*
     * 申请信息状态（0-待审核 1-审核通过 2-审核未通过）
     */
    private Integer applyStatus;

    /*
     * 申请信息被审核的时间
     */
    private String applyAudittime;

    /**
     * 申请方id
     * （若是参加活动申请或加入志愿者团队申请，则该列为志愿者id；
     * 若是发布志愿活动申请，则该列为志愿活动id；
     * 若是志愿单位注册申请，则该列为志愿单位id；
     * 若是志愿者团队注册申请，则该列为志愿者团队id）
     */
    private Integer fkApplicantId;

    /**
     * 申请方类型（0-志愿者、1-志愿者团队，2-志愿单位，3-志愿活动）
     */
    private Integer fkApplicantType;

    /**
     * 审核该条申请信息的人的id
     * （若是参加活动申请，则该列为志愿单位id；
     * 若是加入志愿团队申请，则该列为志愿者团队id；
     * 若是发布志愿活动、志愿者团队注册、志愿单位注册申请，则该列为管理员id）
     */
    private Integer fkAuditorId;

    /*
     * 审核该条申请信息的人的类型（1-志愿者团队，2-志愿单位，3-管理员）
     */
    private Integer fkAuditorType;

    /*
     * 只在志愿者申请参加活动和志愿单位发布活动时使用该字段，主要用于存储被申请的志愿活动id，以及后续判断一个志愿者是否已申请一个志愿活动
     */
    private Integer fkAppliedActivity;

    /*
     * 申请人的对象信息（用于存放申请类型为申请加入团队、志愿者团队注册、志愿单位注册的申请人对象信息）
     */
    @TableField(exist = false)
    private Object applierObject;

    /*
     * 申请携带的志愿活动信息（用于存放申请类型为申请参加活动、志愿活动发布的申请信息所携带的志愿活动对象信息）
     */
    @TableField(exist = false)
    private ActivityInfo appliedActive;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}