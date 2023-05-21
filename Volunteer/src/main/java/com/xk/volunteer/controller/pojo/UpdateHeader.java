package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Rainvex
 * @Date 2022/8/8
 * @Name UpdateHeader
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//更新团队/单位负责人的请求体实体类
public class UpdateHeader {
    private String name;
    private String personalEmail;
    private String personalPhone;
    private String verifyCode;
    private String certificateNo;
    private String certificatePicture;
    private Integer userId;
    private Integer userType;
    private String updateHeadType;
}
