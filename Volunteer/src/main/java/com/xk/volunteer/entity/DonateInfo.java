package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 捐赠信息表
 * @TableName t_donate_info
 */
@TableName(value ="t_donate_info")
@Data
public class DonateInfo implements Serializable {
    /**
     * 捐赠信息id
     */
    @TableId(type = IdType.AUTO)
    private Integer donateId;

    /**
     * 捐赠金额
     */
    private String donateAmount;

    /**
     * 捐赠时间
     */
    private String donateDate;

    /**
     * 捐赠用户
     */
    private Integer fkDonateUser;

    @TableField(exist = false)
    private String donateUserName;

    /**
     * 捐赠用户类型
     */
    private Integer fkDonateUsertype;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}