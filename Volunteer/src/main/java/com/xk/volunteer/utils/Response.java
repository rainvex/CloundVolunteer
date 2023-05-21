package com.xk.volunteer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/6
 * @Name VolunteerController
 * 响应体实体类
 */
public class Response {
    private Integer code;
    private String message;
    private Map<String, Object> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static Response SUCCEED() {
        Response response = new Response();
        response.setData(new HashMap<String, Object>(32));
        response.setCode(200);
        response.setMessage("服务器响应成功");
        return response;
    }

    public static Response SUCCEED(String msg) {
        Response response = new Response();
        response.setData(new HashMap<String, Object>(32));
        response.setCode(200);
        response.setMessage(msg);
        return response;
    }

    public static Response DEFEAT() {
        Response response = new Response();
        response.setData(new HashMap<String, Object>(16));
        response.setCode(400);
        response.setMessage("服务器响应失败");
        return response;
    }

    public static Response DEFEAT(Integer statusCode, String msg) {
        Response response = new Response();
        response.setData(new HashMap<String, Object>(16));
        response.setCode(statusCode);
        response.setMessage(msg);
        return response;
    }

    public final Response carry(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }
}
