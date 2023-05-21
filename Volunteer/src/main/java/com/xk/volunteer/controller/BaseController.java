package com.xk.volunteer.controller;

import cn.hutool.core.util.StrUtil;
import com.xk.volunteer.controller.pojo.*;
import com.xk.volunteer.service.BaseService;
import com.xk.volunteer.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author Rainvex
 * @Date 2022/7/27
 * @Name BaseController
 */
@RestController
@RequestMapping("/base")
public class BaseController {
    @Autowired
    private BaseService baseService;

    @GetMapping("/index")
    public Response index() {
        Map<String, Object> indexData = baseService.index();
        return Response.SUCCEED().carry("indexData",indexData);
    }

    @PostMapping("/getVerifyCode")
    public Object getVerifyCode(@RequestParam("phone") String phone,
                                @RequestParam(value = "userType",required = false) Integer userType,
                                @RequestParam("verifyLogo") String verifyLogo) {
        if (StrUtil.isBlank(phone)) {
            return Response.DEFEAT(400,"输入的手机号码为空");
        }
        String verifyCode = baseService.getVerifyCode(phone, userType, verifyLogo);
        switch (verifyCode) {
            case "该用户不存在":
                return Response.DEFEAT(410, "error:该用户不存在");
            case "操作过于频繁":
                return Response.DEFEAT(411, "error:操作过于频繁");
            case "系统发生错误":
                return Response.DEFEAT(500, "error:系统发生错误");
            default:
                return Response.SUCCEED().carry("sendStatus", verifyCode);
        }
    }

    @PostMapping("/login")
    public Response login(@RequestBody LoginUser loginUser) {
        String token = baseService.login(loginUser);
        switch (token) {
            case "":
                return Response.DEFEAT(430, "用户不存在或账号已被停用");
            case "验证码不存在或已过期":
                return Response.DEFEAT(420, "验证码不存在或已过期");
            case "验证码不正确":
                return Response.DEFEAT(421, "验证码不正确");
            default: return Response.SUCCEED().carry("token",token);
        }
    }

    @PostMapping("/register")
    public Response register(@RequestBody RegisterUser registerUser) {
        String register = baseService.register(registerUser);
        switch (register) {
            case "验证码不存在或已过期":
                return Response.DEFEAT(420, "验证码不存在或已过期");
            case "验证码不正确":
                return Response.DEFEAT(421, "验证码不正确");
            case "发生错误，注册失败":
                return Response.DEFEAT(500, "发生错误，注册失败");
            default: return Response.SUCCEED().carry("registerResult",register);
        }
    }

    @PostMapping("/image/upload")
    public Response uploadImage(@RequestParam("file") MultipartFile multipartFile) {
        String uploadImage = baseService.uploadImage(multipartFile);
        if (StrUtil.equals(uploadImage,"上传失败")) {
            return Response.DEFEAT(500,uploadImage);
        }
        return Response.SUCCEED().carry("filepath", uploadImage);
    }

    @PostMapping("/image/remove")
    public Response removeImage(@RequestParam("pictureUrl") String pictureUrl) {
        boolean removeImage = baseService.removeImage(pictureUrl);
        if (removeImage) {
            return Response.SUCCEED();
        }
        return Response.DEFEAT(500,"出现错误，删除图片失败");
    }

    @PostMapping("/getUserProfile")
    public Response getUserProfile(@RequestParam("userId") Integer userId,
                                   @RequestParam("userType") Integer userType) {
        Map<Object, Object> userProfile = baseService.getUserProfile(userId, userType);
        if (userProfile != null) {
            return Response.SUCCEED().carry("userProfile", userProfile);
        }
        return Response.DEFEAT(430,"该用户不存在或账号已被停用");
    }

    @PostMapping("/updateUser")
    public Response updateUser(@RequestBody UpdateUser updateUser) {
        String newToken = baseService.updateUser(updateUser);
        if (!StrUtil.equals(newToken,""))
            return Response.SUCCEED().carry("newToken",newToken);
        return Response.DEFEAT();
    }

    @PostMapping("/updateHead")
    public Response updateHed(@RequestBody UpdateHeader updateHeader) {
        String updateHead = baseService.updateHead(updateHeader);
        switch (updateHead) {
            case "验证码不存在或已过期":
                return Response.DEFEAT(420, "验证码不存在或已过期");
            case "验证码不正确":
                return Response.DEFEAT(421, "验证码不正确");
            case "success":
                return Response.SUCCEED();
            case "failure":
                return Response.DEFEAT();
        }
        return Response.DEFEAT();
    }

    @PostMapping("/modifyPassword")
    public Response modifyPassword(@RequestBody ModifyPassword modifyPassword) {
        String password = baseService.modifyPassword(modifyPassword);
        if (StrUtil.equals(password,"success")) {
            return Response.SUCCEED();
        } else if (StrUtil.equals(password,"不存在该用户")) {
            return Response.DEFEAT(400,"不存在该用户");
        } else if (StrUtil.equals(password,"旧密码错误")) {
            return Response.DEFEAT(400,"旧密码错误");
        }
        return Response.DEFEAT(500,"系统错误，修改失败");
    }

    @PostMapping("/changeUserStatus")
    public Response changeUserStatus(@RequestParam("userId") Integer userId,
                                     @RequestParam("userType") Integer userType,
                                     @RequestParam("changeStatus") Integer changeStatus) {
        boolean changeUserStatus = baseService.changeUserStatus(userId, userType, changeStatus);
        if (changeUserStatus) return Response.SUCCEED();
        return Response.DEFEAT(500,"修改用户状态出错");
    }

    @PostMapping("/deleteAccount")
    public Response deleteAccount(@RequestParam("userIds") List<Integer> userIds,
                                  @RequestParam("userType") Integer userType) {
        boolean deleteAccount = baseService.deleteAccount(userIds, userType);
        if (deleteAccount) return Response.SUCCEED();
        return Response.DEFEAT();
    }
}
