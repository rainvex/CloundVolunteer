package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.controller.pojo.LoginUser;
import com.xk.volunteer.controller.pojo.ModifyPassword;
import com.xk.volunteer.controller.pojo.RegisterUser;
import com.xk.volunteer.controller.pojo.UpdateUser;
import com.xk.volunteer.entity.VolunteersInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_volunteers_info(志愿者信息表)】的数据库操作Service
* @createDate 2022-07-22 16:26:10
*/
public interface VolunteersInfoService extends IService<VolunteersInfo> {
    Long getVolunteerCount();

    Long getTotalHours();

    Integer register(RegisterUser registerUser);

    String login(LoginUser loginUser);

    Map<Object,Object> queryVolunteerProfile(Integer userId);

    String getVolunteerNickName(Integer userId);

    List<VolunteersInfo> getMostFiveOrderByHours();

    Page<VolunteersInfo> getVolunteerByCondition(String province, String city, Page<VolunteersInfo> page);

    String updateInfo(UpdateUser updateUser);

    String modifyPassword(ModifyPassword modifyPassword);

    Map<Object,Object> judgeTeam(Integer userId,Integer detailId,Integer detailType);

    String auth(MultipartFile file, Integer userId);

    boolean changeStatus(Integer userId, Integer status);

    boolean deleteAccount(List<Integer> userIds);
}
