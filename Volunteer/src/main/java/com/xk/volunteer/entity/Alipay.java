package com.xk.volunteer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alipay {
    private Integer userId;
    private Integer userType;
    private String out_trade_no;
    private String subject;
    private String total_amount;
    private String body;
    private String timeout_express = "10m";
    private String product_code = "FAST_INSTANT_TRADE_PAY";
}
