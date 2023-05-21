package com.xk.volunteer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xk.volunteer.config.AliPayConfig;
import com.xk.volunteer.entity.Alipay;
import com.xk.volunteer.entity.DonateInfo;
import com.xk.volunteer.service.BaseService;
import com.xk.volunteer.service.DonateInfoService;
import com.xk.volunteer.mapper.DonateInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Administrator
* @description 针对表【t_donate_info(捐赠信息表)】的数据库操作Service实现
* @createDate 2022-08-11 19:41:52
*/
@Service
@Transactional
public class DonateInfoServiceImpl extends ServiceImpl<DonateInfoMapper, DonateInfo>
    implements DonateInfoService{
    private Integer userId;
    private Integer userType;

    @Autowired
    private AliPayConfig aliPayConfig;
    @Autowired
    private DonateInfoMapper donateInfoMapper;
    @Autowired
    private BaseService baseService;

    @Override
    public String initAliPayPage(Alipay alipay) {
        //创建支付宝客户端
        AlipayClient client = new DefaultAlipayClient(aliPayConfig.getServerUrl(),aliPayConfig.getAppId(),aliPayConfig.getAppPrivateKey(),
                aliPayConfig.getFormat(),aliPayConfig.getCharset(),aliPayConfig.getAlipayPublicKey(),aliPayConfig.getSignType());
        //创建支付宝网页支付对象
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //将发起请求的用户id和类型保存
        userId = alipay.getUserId();
        userType = alipay.getUserType();
        //创建支付宝网页支付视图对象
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(alipay.getOut_trade_no());
        model.setSubject(alipay.getSubject());
        model.setTotalAmount(alipay.getTotal_amount());
        model.setBody(alipay.getBody());
        model.setTimeoutExpress(alipay.getTimeout_express());
        model.setProductCode(alipay.getProduct_code());

        request.setBizModel(model);
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        try {
            //发送请求，调用支付宝接口
            AlipayTradePagePayResponse response = client.pageExecute(request);
            if (response.isSuccess()) {
                //若成功调用则返回响应的字符串，将其返回到前端进行渲染
                return response.getBody();
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String donateCallback(Map<String, String> params) {
        boolean signVerified = false;
        try {
            // 调用SDK验证签名（支付宝公钥）
            signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipayPublicKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //验证签名通过
        if(signVerified){
            //在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED支付宝才会认定为买家付款成功。
            if(StrUtil.equals(params.get("trade_status"),"TRADE_SUCCESS") || StrUtil.equals(params.get("trade_status"),"TRADE_FINISHED")){
                //获取到捐赠金额
                String total_amount = params.get("total_amount");
                DonateInfo donateInfo = new DonateInfo();
                donateInfo.setDonateAmount(total_amount);
                donateInfo.setDonateDate(DateUtil.now());
                donateInfo.setFkDonateUser(userId);
                donateInfo.setFkDonateUsertype(userType);
                int insert = donateInfoMapper.insert(donateInfo);
                if (insert > 0) return "success";
            }
        }
        System.out.println("支付未成功");
        return "failure";
    }

    @Override
    public Map<Object,Object> getDonateRecord(Integer userId, Integer userType, Page<DonateInfo> page) {
        QueryWrapper<DonateInfo> wrapper = new QueryWrapper<>();
        if (userType == 3) {
            wrapper.orderByDesc("DONATE_DATE");
        } else {
            wrapper.eq("FK_DONATE_USER",userId)
                    .eq("FK_DONATE_USERTYPE",userType)
                    .orderByDesc("DONATE_DATE");
        }
        Page<DonateInfo> donateInfoPage = donateInfoMapper.selectPage(page, wrapper);
        List<DonateInfo> donateRecords = donateInfoPage.getRecords();
        if (donateRecords.size() != 0) {
            donateRecords.forEach(donateInfo -> {
                donateInfo.setDonateUserName(baseService.getUserNickName(donateInfo.getFkDonateUser(),donateInfo.getFkDonateUsertype()));
            });
        }
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("donateRecord",donateRecords);
        map.put("total",donateInfoPage.getTotal());
        return map;
    }

    @Override
    public boolean deleteDonate(List<Integer> donateIds) {
        return donateInfoMapper.deleteBatchIds(donateIds) > 0;
    }

    @Override
    public Long statisticsDonateCount() {
        return donateInfoMapper.statisticsDonateCount();
    }
}




