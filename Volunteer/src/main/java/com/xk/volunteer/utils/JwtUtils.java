package com.xk.volunteer.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;


public class JwtUtils {

    public static String getToken(Map<String, String> map,String sign) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); //1天过期
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        return builder
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(sign));//以用户密码作为token签名
    }

    public static DecodedJWT verity(String token,String sign) {
        return JWT.require(Algorithm.HMAC256(sign)).build().verify(token);
    }
}
