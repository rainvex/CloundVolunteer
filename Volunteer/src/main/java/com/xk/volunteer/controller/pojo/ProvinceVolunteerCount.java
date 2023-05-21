package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Rainvex
 * @Date 2023/2/19
 * @Name ProvinceVolunteerCount
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceVolunteerCount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String provinceName;
    private Long volunteerCount;
}
