package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Rainvex
 * @Date 2022/8/8
 * @Name UpdateUser
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//更新用户基本信息的请求体实体类
public class UpdateUser {
    private String avatar;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String political;
    private String professional;
    private String degree;
    private String serviceObject;
    private String organizationType;
    private String introduction;
    private Integer userId;
    private Integer userType;
}
