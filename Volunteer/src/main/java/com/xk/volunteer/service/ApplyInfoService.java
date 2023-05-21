package com.xk.volunteer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xk.volunteer.entity.ApplyInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【t_apply_info(申请信息表（志愿者申请加入志愿者团队、志愿者申请参加志愿活动）)】的数据库操作Service
* @createDate 2022-08-05 14:52:01
*/
public interface ApplyInfoService extends IService<ApplyInfo> {
    Integer insertApply(ApplyInfo applyInfo);

    Map<Object,Object> getActivityApply(Integer userId, Integer userType, Integer currentPage);

    Boolean getIsApplied(Integer userId,Integer activeId);

    Integer cancelApplyActivity(Integer userId,Integer userType,Integer activeId);

    Map<Object,Object> getAuditInfo(Integer userId, Integer userType, Integer applyType, Page<ApplyInfo> page);

    ApplyInfo queryApplyById(Integer applyId);

    boolean deleteApply(List<Integer> applyIds);

    boolean auditApply(Integer applyId, String auditCommand);
}
