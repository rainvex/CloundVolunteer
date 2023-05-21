package com.xk.volunteer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 管理员信息表
 * @TableName t_admin_info
 */
@TableName(value ="t_admin_info")
@Data
public class AdminInfo implements Serializable {
    /**
     * 管理员id
     */
    @TableId(type = IdType.AUTO)
    private Integer adminId;

    /**
     * 管理员名字
     */
    private String adminName;

    /**
     * 管理员密码
     */
    private String adminPassword;

    /**
     * 管理员头像地址
     */
    private String adminAvatar;

    /**
     * 管理员手机号
     */
    private String adminPhone;

    /**
     * 管理员邮箱
     */
    private String adminEmail;

    /**
     * 管理员身份标识
     */
    private Integer adminType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}