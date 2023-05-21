package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 志愿活动被收藏的信息表
 * @TableName t_star_info
 */
@TableName(value ="t_star_info")
@Data
public class StarInfo implements Serializable {
    /**
     * 收藏信息id
     */
    @TableId(type = IdType.AUTO)
    private Integer starId;

    /**
     * 被收藏的志愿活动id
     */
    private Integer fkStarActivity;

    @TableField(exist = false)
    private ActivityInfo activityInfo;

    /**
     * 收藏的志愿者id
     */
    private Integer fkStarVolunteer;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}