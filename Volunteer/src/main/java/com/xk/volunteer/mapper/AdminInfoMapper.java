package com.xk.volunteer.mapper;
import java.util.List;

import com.xk.volunteer.entity.AdminInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author Administrator
* @description 针对表【t_admin_info(管理员信息表)】的数据库操作Mapper
* @createDate 2022-07-22 16:26:09
* @Entity com.xk.volunteer.entity.AdminInfo
*/
@Repository
public interface AdminInfoMapper extends BaseMapper<AdminInfo> {

    AdminInfo selectOneByAdminPhone(@Param("adminPhone") String adminPhone);

    AdminInfo selectOneByAdminEmailAndAdminPassword(@Param("adminEmail") String adminEmail, @Param("adminPassword") String adminPassword);
}




