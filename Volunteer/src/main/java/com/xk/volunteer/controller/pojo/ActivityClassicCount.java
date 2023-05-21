package com.xk.volunteer.controller.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Rainvex
 * @Date 2022/12/29
 * @Name ActivityClassicCount
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityClassicCount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String activityClassic;
    private Long classicCount;
}
