package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Rainvex
 * @Date 2022/7/28
 * @Name LoginUser
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String phone;
    private String password;
    private String verifyCode;
    private Integer userType;
    private Integer loginType;
}
