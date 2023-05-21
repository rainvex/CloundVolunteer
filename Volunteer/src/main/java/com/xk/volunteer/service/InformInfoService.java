package com.xk.volunteer.service;

import com.xk.volunteer.entity.InformInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_inform_info】的数据库操作Service
* @createDate 2022-12-26 13:37:51
*/
public interface InformInfoService extends IService<InformInfo> {
    List<InformInfo> selectAllInform(Integer userType,Integer currentPage);

    List<InformInfo> selectInformHighView();

    InformInfo selectInformInfo(Integer informId);

    String publicInform(InformInfo informInfo);

    boolean changeInformStatus(Integer informId,Integer changedStatus);

    boolean deleteInform(List<Integer> informIds);
}
