package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Rainvex
 * @Date 2022/8/8
 * @Name ModifyPassword
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPassword {
    private Integer userId;
    private Integer userType;
    private String email;
    private String oldPassword;
    private String newPassword;
}
