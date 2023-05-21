package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 志愿者成功参加志愿活动的信息表
 * @TableName t_join_info
 */
@TableName(value ="t_join_info")
@Data
public class JoinInfo implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer joinId;

    /**
     * 志愿活动的完成状态（0-未完成 1-已完成）
     */
    private Integer joinStatus;

    /**
     * 某个志愿活动的id
     */
    private Integer fkJoinActivity;

    @TableField(exist = false)
    private ActivityInfo activityInfo;

    /**
     * 参加活动的志愿者id
     */
    private Integer fkJoinVolunteer;

    @TableField(exist = false)
    private VolunteersInfo volunteersInfo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}