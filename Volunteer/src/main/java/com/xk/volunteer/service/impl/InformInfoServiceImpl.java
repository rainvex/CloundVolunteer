package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.entity.InformInfo;
import com.xk.volunteer.service.InformInfoService;
import com.xk.volunteer.mapper.InformInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_inform_info】的数据库操作Service实现
* @createDate 2022-12-26 13:37:51
*/
@Service
public class InformInfoServiceImpl extends ServiceImpl<InformInfoMapper, InformInfo>
    implements InformInfoService{

    @Autowired
    private InformInfoMapper informInfoMapper;

    @Override
    public List<InformInfo> selectAllInform(Integer userType,Integer currentPage) {
        if (userType == null){
            return informInfoMapper.selectPage(new Page<InformInfo>(currentPage, 15),new QueryWrapper<InformInfo>().eq("INFORM_STATUS",1).orderByDesc(("INFORM_PUBLIC"))).getRecords();
        } else if (userType == 3){
            return informInfoMapper.selectPage(new Page<InformInfo>(currentPage, 12),new QueryWrapper<InformInfo>().orderByDesc(("INFORM_PUBLIC"))).getRecords();
        }
        return null;
    }

    @Override
    public List<InformInfo> selectInformHighView() {
        return informInfoMapper.selectInformHighView();
    }

    @Override
    public InformInfo selectInformInfo(Integer informId) {
        InformInfo informInfo = informInfoMapper.selectOne(new QueryWrapper<InformInfo>().eq("INFORM_ID", informId));
        informInfo.setInformView(informInfo.getInformView() + 1);
        informInfoMapper.updateById(informInfo);
        return informInfo;
    }

    @Override
    public String publicInform(InformInfo informInfo) {
        informInfo.setInformPublic(DateUtil.today());
        if (informInfo.getInformId() == 0) {
            int insert = informInfoMapper.insert(informInfo);
            if (insert > 0) {
                return "发布志愿资讯成功";
            } else {
                return "发布志愿资讯失败";
            }
        } else {
            int update = informInfoMapper.update(informInfo, new UpdateWrapper<InformInfo>().eq("INFORM_ID", informInfo.getInformId()));
            if (update > 0) {
                return "更新志愿资讯成功";
            } else {
                return "更新志愿资讯失败";
            }
        }
    }

    @Override
    public boolean changeInformStatus(Integer informId, Integer changedStatus) {
        UpdateWrapper<InformInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("INFORM_ID",informId)
                .set("INFORM_STATUS",changedStatus);
        return informInfoMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean deleteInform(List<Integer> informIds) {
        return informInfoMapper.deleteBatchIds(informIds) > 0;
    }
}




