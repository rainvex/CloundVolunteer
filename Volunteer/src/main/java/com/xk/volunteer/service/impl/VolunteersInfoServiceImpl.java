package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.controller.pojo.LoginUser;
import com.xk.volunteer.controller.pojo.ModifyPassword;
import com.xk.volunteer.controller.pojo.RegisterUser;
import com.xk.volunteer.controller.pojo.UpdateUser;
import com.xk.volunteer.entity.*;
import com.xk.volunteer.mapper.*;
import com.xk.volunteer.service.VolunteersInfoService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Administrator
* @description 针对表【t_volunteers_info(志愿者信息表)】的数据库操作Service实现
* @createDate 2022-07-22 16:26:10
*/
@Service
@Transactional
public class VolunteersInfoServiceImpl extends ServiceImpl<VolunteersInfoMapper, VolunteersInfo>
    implements VolunteersInfoService{
    private static final String REDIS_VERIFY_CODE_PREFIX = "USER_PHONE:";

    @Autowired
    private VolunteersInfoMapper volunteersInfoMapper;
    @Autowired
    private TeamInfoMapper teamInfoMapper;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private DonateInfoMapper donateInfoMapper;
    @Autowired
    private JoinInfoMapper joinInfoMapper;
    @Autowired
    private MessageInfoMapper messageInfoMapper;
    @Autowired
    private StarInfoMapper starInfoMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Long getVolunteerCount() {
        return volunteersInfoMapper.selectCount(new QueryWrapper<VolunteersInfo>().eq("VOLUNTEERS_STATUS",1));
    }

    @Override
    public Long getTotalHours() {
        return volunteersInfoMapper.selectTotalHours();
    }

    @Override
    public List<VolunteersInfo> getMostFiveOrderByHours() {
        return volunteersInfoMapper.selectVolunteersIdAndVolunteersUsernameAndVolunteersHoursOrderByVolunteersHoursDesc();
    }

    @Override
    public Integer register(RegisterUser registerUser) {
        VolunteersInfo volunteersInfo = new VolunteersInfo();
        volunteersInfo.setVolunteersEmail(registerUser.getPersonalEmail());
        volunteersInfo.setVolunteersPhone(registerUser.getPersonalPhone());
        volunteersInfo.setVolunteersPassword(registerUser.getPassword());
        volunteersInfo.setVolunteersUsername(registerUser.getUsername());
        volunteersInfo.setVolunteersPolitical(registerUser.getPolitical());
        volunteersInfo.setVolunteersProfessional(registerUser.getProfessional());
        volunteersInfo.setVolunteersDegree(registerUser.getDegree());
        volunteersInfo.setVolunteersAddress(registerUser.getAddress());
        //从志愿者注册填写的信息中获取所属省份
        String province = "";
        Matcher matcher = Pattern.compile("(台湾|香港|湖北|上海|广东|吉林|北京|四川|海南|重庆|河南|内蒙古|福建|浙江|陕西|云南|黑龙江|山西|山东|江苏|辽宁|湖南|河北|天津|新疆|广西|江西|贵州|安徽|澳门|甘肃|西藏|青海|宁夏)").matcher(registerUser.getAddress());
        while (matcher.find()) {
            province = matcher.group();
        }
        volunteersInfo.setVolunteersProvince(province);
        volunteersInfo.setVolunteersSimple(registerUser.getIntroduction());
        volunteersInfo.setVolunteersAvatar("https://rainvex-1305747533.cos.ap-chengdu.myqcloud.com/GraduationDesign/defaulAvatar.jpg");
        volunteersInfo.setVolunteersRegister(DateUtil.today());
        return volunteersInfoMapper.insert(volunteersInfo);
    }

    @Override
    public String login(LoginUser loginUser) {
        VolunteersInfo volunteersInfo = null;
        //根据登陆方式进行判断
        //邮箱登录
        if (loginUser.getLoginType() == 0) {
            volunteersInfo = volunteersInfoMapper.selectOneByVolunteersEmailAndVolunteersPasswordAndVolunteersStatus(loginUser.getEmail(), loginUser.getPassword(), 1);
        } else if (loginUser.getLoginType() == 1) {
            //手机号登陆
            //判断验证码是否正确
            String verifyCode = redisTemplate.opsForValue().get(REDIS_VERIFY_CODE_PREFIX + loginUser.getPhone());
            if (StrUtil.isEmpty(verifyCode)) {
                return "验证码不存在或已过期";
            }
            if (!loginUser.getVerifyCode().equals(verifyCode)) {
                return "验证码不正确";
            }
            //验证码相等，删除验证码
            redisTemplate.delete(REDIS_VERIFY_CODE_PREFIX + loginUser.getPhone());
            volunteersInfo = volunteersInfoMapper.selectOneByVolunteersPhoneAndVolunteersStatus(loginUser.getPhone(),1);
        }
        //根据查询结果判断是否登陆成功
        if (volunteersInfo != null) {
            return JWT.create()
                    .setPayload("userId",volunteersInfo.getVolunteersId())
                    .setPayload("userAvatar",volunteersInfo.getVolunteersAvatar())
                    .setPayload("userNickName",volunteersInfo.getVolunteersUsername())
                    .setPayload("userRegisterTime",volunteersInfo.getVolunteersRegister())
                    .setPayload("userType",volunteersInfo.getVolunteersType())
                    .setPayload("userGender",volunteersInfo.getVolunteersGender())
                    .setKey(volunteersInfo.getVolunteersPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public Map<Object, Object> queryVolunteerProfile(Integer userId) {
        VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(userId);
        if (volunteersInfo != null) {
            String teamName = "";
            if (volunteersInfo.getFkVolunteersTeam() != null)
                teamName = teamInfoMapper.selectTeamNameByTeamIdAndTeamStatus(volunteersInfo.getFkVolunteersTeam(),1).getTeamName();
            if (StrUtil.isNotBlank(volunteersInfo.getVolunteersCard()))
                volunteersInfo.setVolunteersCard(volunteersInfo.getVolunteersCard().substring(0, 6) + "**********" + volunteersInfo.getVolunteersCard().substring(16));
            HashMap<Object, Object> map = new HashMap<>(25);
            map.put("userId",volunteersInfo.getVolunteersId());
            map.put("userAvatar",volunteersInfo.getVolunteersAvatar());
            map.put("userTeam",teamName);
            map.put("userActiveCount",volunteersInfo.getVolunteersActivecount());
            map.put("userBreak",volunteersInfo.getVolunteersBreak());
            map.put("userHours",volunteersInfo.getVolunteersHours());
            map.put("userNickName",volunteersInfo.getVolunteersUsername());
            map.put("userEmail",volunteersInfo.getVolunteersEmail());
            map.put("userPhone",volunteersInfo.getVolunteersPhone());
            map.put("userAddress",volunteersInfo.getVolunteersAddress());
            map.put("userPolitical",volunteersInfo.getVolunteersPolitical());
            map.put("userProfessional",volunteersInfo.getVolunteersProfessional());
            map.put("userDegree",volunteersInfo.getVolunteersDegree());
            map.put("userRegisterTime",volunteersInfo.getVolunteersRegister());
            map.put("userIntroduction",volunteersInfo.getVolunteersSimple());
            map.put("userName",volunteersInfo.getVolunteersName());
            map.put("userBirth",volunteersInfo.getVolunteersBirth());
            map.put("userGender",volunteersInfo.getVolunteersGender());
            map.put("userNational",volunteersInfo.getVolunteersNational());
            map.put("userNative",volunteersInfo.getVolunteersNative());
            map.put("userCertificate",volunteersInfo.getVolunteersCard());
            map.put("userType",volunteersInfo.getVolunteersType());
            return map;
        }
        return null;
    }

    @Override
    public String getVolunteerNickName(Integer userId) {
        return volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>().select("VOLUNTEERS_USERNAME").eq("VOLUNTEERS_ID",userId)).getVolunteersUsername();
    }

    @Override
    public Page<VolunteersInfo> getVolunteerByCondition(String province, String city, Page<VolunteersInfo> page) {
        QueryWrapper<VolunteersInfo> wrapper = new QueryWrapper<>();
        wrapper.select("VOLUNTEERS_ID,VOLUNTEERS_AVATAR,VOLUNTEERS_USERNAME,VOLUNTEERS_HOURS,VOLUNTEERS_ADDRESS");
        wrapper.eq("VOLUNTEERS_STATUS",1);
        //当查询条件中省不为空
        if (StrUtil.isNotEmpty(province)) {
            StringBuffer area = new StringBuffer(province);
            //当查询条件中市不为空
            if (StrUtil.isNotEmpty(city))
                area.append(city);
            wrapper.likeRight("VOLUNTEERS_ADDRESS",area);
        }
        return volunteersInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public String updateInfo(UpdateUser updateUser) {
        VolunteersInfo volunteersInfo = new VolunteersInfo();
        volunteersInfo.setVolunteersId(updateUser.getUserId());
        volunteersInfo.setVolunteersAvatar(updateUser.getAvatar());
        volunteersInfo.setVolunteersUsername(updateUser.getUsername());
        volunteersInfo.setVolunteersEmail(updateUser.getEmail());
        volunteersInfo.setVolunteersPhone(updateUser.getPhone());
        volunteersInfo.setVolunteersAddress(updateUser.getAddress());
        volunteersInfo.setVolunteersPolitical(updateUser.getPolitical());
        volunteersInfo.setVolunteersProfessional(updateUser.getProfessional());
        volunteersInfo.setVolunteersDegree(updateUser.getDegree());
        volunteersInfo.setVolunteersSimple(updateUser.getIntroduction());
        if (volunteersInfoMapper.updateById(volunteersInfo) > 0) {
            VolunteersInfo newVolunteer = volunteersInfoMapper.selectById(volunteersInfo.getVolunteersId());
            return JWT.create()
                    .setPayload("userId",newVolunteer.getVolunteersId())
                    .setPayload("userAvatar",volunteersInfo.getVolunteersAvatar())
                    .setPayload("userNickName",newVolunteer.getVolunteersUsername())
                    .setPayload("userRegisterTime",newVolunteer.getVolunteersRegister())
                    .setPayload("userType",newVolunteer.getVolunteersType())
                    .setPayload("userGender",volunteersInfo.getVolunteersGender())
                    .setKey(newVolunteer.getVolunteersPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public String modifyPassword(ModifyPassword modifyPassword) {
        //如果邮箱不为空，说明是忘记密码情况下的修改
        if (StrUtil.isNotBlank(modifyPassword.getEmail())) {
            //判断输入的邮箱是否存在
            //如果有该用户
            if (volunteersInfoMapper.exists(new QueryWrapper<VolunteersInfo>().eq("VOLUNTEERS_EMAIL",modifyPassword.getEmail()))) {
                VolunteersInfo volunteersInfo = volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>().eq("VOLUNTEERS_EMAIL", modifyPassword.getEmail()));
                volunteersInfo.setVolunteersPassword(modifyPassword.getNewPassword());
                int update = volunteersInfoMapper.updateById(volunteersInfo);
                if (update > 0) return "success";
                return "error";
            }
            return "不存在该用户";
        }
        //邮箱为空，说明是修改密码
        //根据修改密码的用户id查询
        VolunteersInfo volunteersInfo = volunteersInfoMapper.selectOne(new QueryWrapper<VolunteersInfo>().eq("VOLUNTEERS_ID", modifyPassword.getUserId()));
        //判断输入的旧密码是否相等
        //如果不相等
        if (!StrUtil.equals(volunteersInfo.getVolunteersPassword(),modifyPassword.getOldPassword())) {
            return "旧密码错误";
        }
        //相等
        volunteersInfo.setVolunteersPassword(modifyPassword.getNewPassword());
        int update = volunteersInfoMapper.updateById(volunteersInfo);
        if (update > 0) return "success";
        return "error";
    }

    @Override
    public Map<Object,Object> judgeTeam(Integer userId, Integer detailId,Integer detailType) {
        boolean isApply = false;
        boolean isApplyCurrent = false;
        boolean isMember = false;
        boolean hasTeam = false;
        Integer teamId = 0;
        if (detailType == 0) {
            teamId = volunteersInfoMapper.selectById(detailId).getFkVolunteersTeam();
        } else if (detailType == 1) {
            teamId = detailId;
        }
        VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(userId);
        if ((volunteersInfo.getFkVolunteersTeam() == null) && (volunteersInfo.getVolunteersIsapplyteam() == 1)) {
            isApply = true;
            ApplyInfo applyInfo = applyInfoMapper.selectOne(new QueryWrapper<ApplyInfo>()
                    .eq("APPLY_STATUS", 0)
                    .eq("APPLY_TYPE", 1)
                    .eq("FK_APPLICANT_ID", userId)
                    .eq("FK_APPLICANT_TYPE", 0)
                    .eq("FK_AUDITOR_ID", teamId)
                    .eq("FK_AUDITOR_TYPE", 1));
            if (applyInfo != null) {
                isApplyCurrent = true;
            }
        } else if (volunteersInfo.getFkVolunteersTeam() != null) {
            hasTeam = true;
            if (Objects.equals(volunteersInfo.getFkVolunteersTeam(), teamId)) {
                isMember = true;
            }
        }
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("isApply",isApply);
        map.put("isApplyCurrent",isApplyCurrent);
        map.put("isMember",isMember);
        map.put("hasTeam",hasTeam);
        return map;
    }

    @Override
    public String auth(MultipartFile file, Integer userId) {
        //识别身份证，获取其中信息
        OkHttpClient client = new OkHttpClient();
        RequestBody body = null;
        String responseBody = "";
        try {
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file.getBytes()))
                    .addFormDataPart("key", "")
                    .addFormDataPart("secret", "")
                    .addFormDataPart("typeId", "2")//需修改为对应产品ID
                    .addFormDataPart("format", "json")
                    .build();
            Request request = new Request.Builder()
                    .url("https://netocr.com/api/recog.do")//需修改为对应产品接口url
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            responseBody = response.body().string();
            //响应体结果实例：
            //{
            //  "message":{"status":2,"value":"识别成功"},
            //  "cardsinfo":[
            //    {
            //    "type":"2",
            //    "items":[
            //      {"nID":null,"index":null,"desc":"保留","content":""},
            //      {"nID":null,"index":null,"desc":"姓名","content":"王元青"},
            //      {"nID":null,"index":null,"desc":"性别","content":"男"},
            //      {"nID":null,"index":null,"desc":"民族","content":"汉"},
            //      {"nID":null,"index":null,"desc":"出生","content":"1981-02-21"},
            //      {"nID":null,"index":null,"desc":"住址","content":"浙江省桐庐县***村二组"},
            //      {"nID":null,"index":null,"desc":"公民身份号码","content":"3301******02212239"},
            //      {"nID":null,"index":null,"desc":"头像","content":"头像BASE64编码"}
            //    ]
            //    }
            //  ]
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将识别的结果响应体字符串转为json对象
        JSONObject responseJson = JSONObject.parseObject(responseBody);
        //获取到其中的cardsinfo，它是一个数组，其中包含一个对象，即该数组大小只有1
        Object cardsinfo = responseJson.get("cardsinfo");
        //将cardsinfo数组转为字符串，再转为JSON数组
        JSONArray jsonArray = JSONObject.parseArray(cardsinfo.toString());
        if (!(jsonArray.size() > 0)) return "";
        //获取到JSON数组中的唯一元素（又是一个对象类型），所以此时使用getJSONObject(0)
        //然后再获取到这个对象中键为items的值，返回为一个字符串，而这个items又是一个数组，其中有多个对象
        String items = jsonArray.getJSONObject(0).getString("items");
        //把这个数组字符串转为JSON数组，其中有多个对象。分别存放着需要的不同信息，而content就是存放信息的键
        JSONArray jsonArray1 = JSONObject.parseArray(items);
        VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(userId);
        volunteersInfo.setVolunteersName(jsonArray1.getJSONObject(1).getString("content"));
        volunteersInfo.setVolunteersGender(jsonArray1.getJSONObject(2).getString("content"));
        volunteersInfo.setVolunteersNational(jsonArray1.getJSONObject(3).getString("content"));
        volunteersInfo.setVolunteersBirth(jsonArray1.getJSONObject(4).getString("content"));
        volunteersInfo.setVolunteersNative(jsonArray1.getJSONObject(5).getString("content"));
        volunteersInfo.setVolunteersCard(jsonArray1.getJSONObject(6).getString("content"));
        int update = volunteersInfoMapper.updateById(volunteersInfo);
        if (update > 0) {
            return JWT.create()
                    .setPayload("userId",volunteersInfo.getVolunteersId())
                    .setPayload("userAvatar",volunteersInfo.getVolunteersAvatar())
                    .setPayload("userNickName",volunteersInfo.getVolunteersUsername())
                    .setPayload("userRegisterTime",volunteersInfo.getVolunteersRegister())
                    .setPayload("userType",volunteersInfo.getVolunteersType())
                    .setPayload("userGender",volunteersInfo.getVolunteersGender())
                    .setKey(volunteersInfo.getVolunteersPassword().getBytes())
                    .sign();
        }
        return "";
    }

    @Override
    public boolean changeStatus(Integer userId, Integer status) {
        UpdateWrapper<VolunteersInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("VOLUNTEERS_ID",userId)
                .set("VOLUNTEERS_STATUS",status);
        return volunteersInfoMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean deleteAccount(List<Integer> userIds) {
        if (userIds.size() > 0) {
            userIds.forEach(userId -> {
                VolunteersInfo volunteersInfo = volunteersInfoMapper.selectById(userId);
                //首先，如果志愿者加入了团队，则先退出团队（减少该团队的成员数，活动数，志愿时长数）
                if (volunteersInfo.getFkVolunteersTeam() != null) {
                    TeamInfo teamInfo = teamInfoMapper.selectById(volunteersInfo.getFkVolunteersTeam());
                    teamInfo.setTeamHours(teamInfo.getTeamHours() - volunteersInfo.getVolunteersHours());
                    teamInfo.setTeamCount(teamInfo.getTeamCount() - 1);
                    teamInfo.setTeamActivecount(teamInfo.getTeamActivecount() - volunteersInfo.getVolunteersActivecount());
                    teamInfoMapper.updateById(teamInfo);
                }
                //然后，删除其申请记录（活动申请，入团申请）
                applyInfoMapper.delete(new QueryWrapper<ApplyInfo>()
                        .eq("FK_APPLICANT_ID",userId)
                        .eq("FK_APPLICANT_TYPE",0));
                //然后，删除其捐赠记录
                donateInfoMapper.delete(new QueryWrapper<DonateInfo>()
                        .eq("FK_DONATE_USER",userId)
                        .eq("FK_DONATE_USERTYPE",0));
                //删除成功参加活动的记录
                joinInfoMapper.delete(new QueryWrapper<JoinInfo>()
                        .eq("FK_JOIN_VOLUNTEER",userId));
                //删除消息记录
                messageInfoMapper.delete(new QueryWrapper<MessageInfo>()
                        .eq("FK_MESSAGE_SENDER",userId)
                        .eq("FK_MESSAGE_SENDERTYPE",0)
                        .or(i -> i.eq("FK_MESSAGE_ACCEPTER",userId)
                                .eq("FK_MESSAGE_ACCEPTERTYPE",0)));
                //删除收藏记录
                starInfoMapper.delete(new QueryWrapper<StarInfo>()
                        .eq("FK_STAR_VOLUNTEER",userId));
                //删除志愿者本身信息
                volunteersInfoMapper.deleteById(userId);
            });
            return true;
        }
        return false;
    }
}




