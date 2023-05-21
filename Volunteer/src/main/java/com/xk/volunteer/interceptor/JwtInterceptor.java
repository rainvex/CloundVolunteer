package com.xk.volunteer.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xk.volunteer.service.BaseService;
import com.xk.volunteer.service.impl.BaseServiceImpl;
import com.xk.volunteer.utils.JwtUtils;
import com.xk.volunteer.utils.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private BaseService baseService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        String token = request.getHeader("Authorization");
        Response defeat = Response.DEFEAT(401,"请求需要用户身份认证");
        if (StrUtil.isNotBlank(token)) {
            try {
                //根据token中携带的用户id去获取该用户的密码作为验证token的签名
                Integer userId = JWT.decode(token.substring(7)).getClaim("userId").asInt();
                Integer userType = JWT.decode(token.substring(7)).getClaim("userType").asInt();
                String password = baseService.getPasswordByToken(userId, userType);
                JwtUtils.verity(token.substring(7),password);
                return true;
            } catch (SignatureVerificationException e) {
                e.printStackTrace();
                defeat.carry("msg", "token签名不一致");
            } catch (TokenExpiredException e) {
                e.printStackTrace();
                defeat.carry("msg", "token过期");
            } catch (AlgorithmMismatchException e) {
                e.printStackTrace();
                defeat.carry("msg", "token校验算法不一致");
            } catch (InvalidClaimException e){
                e.printStackTrace();
                defeat.carry("msg","token负载失效");
            } catch (Exception e) {
                e.printStackTrace();
                defeat.carry("msg", "系统错误！");
            }
        } else if (StrUtil.isBlank(token)) {
            defeat.carry("msg","token为空");
        }
        String json = new ObjectMapper().writeValueAsString(defeat);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        return false;
    }
}
