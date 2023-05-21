package com.xk.volunteer;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xk.volunteer.controller.pojo.ActivityClassicCount;
import com.xk.volunteer.entity.ActivityInfo;
import com.xk.volunteer.entity.VolunteersInfo;
import com.xk.volunteer.mapper.ActivityInfoMapper;
import com.xk.volunteer.mapper.ApplyInfoMapper;
import com.xk.volunteer.mapper.VolunteersInfoMapper;
import com.xk.volunteer.service.MessageInfoService;
import com.xk.volunteer.utils.SMSUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class VolunteerApplicationTests {

}
