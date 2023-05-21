package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author Rainvex
 * @Date 2022/7/29
 * @Name RegisterUser
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String personalEmail;
    private String organizationEmail;
    private String personalPhone;
    private String organizationPhone;
    private String password;
    private String verifyCode;
    private String address;
    private String introduction;
    private String political;
    private String professional;
    private String degree;
    private String serviceObject;
    private String organizationType;
    private String provePicture;
    private String headName;
    private String headCertificatePicture;
    private String headCertificateNo;
    private String registerType;
}
