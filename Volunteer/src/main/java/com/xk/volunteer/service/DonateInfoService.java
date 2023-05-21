package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.Alipay;
import com.xk.volunteer.entity.DonateInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_donate_info(捐赠信息表)】的数据库操作Service
* @createDate 2022-08-11 19:41:52
*/
public interface DonateInfoService extends IService<DonateInfo> {
    String initAliPayPage(Alipay alipay);

    String donateCallback(Map<String, String> params);

    Map<Object,Object> getDonateRecord(Integer userId, Integer userType, Page<DonateInfo> page);

    boolean deleteDonate(List<Integer> donateIds);

    Long statisticsDonateCount();
}
