package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName t_inform_info
 */
@TableName(value ="t_inform_info")
@Data
public class InformInfo implements Serializable {
    /**
     * 资讯主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer informId;

    /**
     * 资讯标题
     */
    private String informTitle;

    /**
     * 资讯发布时间
     */
    private String informPublic;

    /**
     * 资讯文章内容
     */
    private String informContent;

    /**
     * 资讯来源
     */
    private String informFrom;

    /**
     * 资讯被点击查看次数
     */
    private Integer informView;

    /**
     * 咨询状态 0-不显示 1-显示
     */
    private Integer informStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}