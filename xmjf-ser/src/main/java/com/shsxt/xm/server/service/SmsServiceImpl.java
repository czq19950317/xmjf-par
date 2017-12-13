package com.shsxt.xm.server.service;

import com.alibaba.fastjson.JSON;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.ISmsService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.PhoneJudge;
import com.shsxt.xm.api.utils.SmsType;
import com.shsxt.xm.server.constant.TaoBaoConstant;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.internal.util.TaobaoContext;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements ISmsService {
    @Resource
    private IBasUserService basUserService;
    @Override
    public void sendPhoneSms(String phone, String code, Integer type) {
        /**
         * 判断手机号是哦否为空
         */
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
        /**
         * 判断手机号是否合法
         */
        AssertUtil.isTrue(!PhoneJudge.isMobiPhoneNum(phone),"请输入正确的手机号");
        /**
         * 判断手机验证码是否为空
         */
        AssertUtil.isTrue(StringUtils.isBlank(code),"手机验证码不能为空");
        /**
         * 判断短信验证类型是否为空
         */
        AssertUtil.isTrue(null==type,"短信验证类型不能为空");
        if(type.equals(SmsType.REGISTER.getType())){
            /**
             * 注册时用户手机号不能重复
             */
            BasUser user = basUserService.queryBasUserByPhone(phone);
            AssertUtil.isTrue(null!=user,"该手机已经被注册");
            doSend(phone,code);
        }
        if(type.equals(SmsType.NOTIFY.getType())){
            doSend(phone,code);
        }

    }

    private void doSend(String phone, String code) {
        TaobaoClient client=new DefaultTaobaoClient(TaoBaoConstant.SERVER_URL,
                TaoBaoConstant.APP_KEY,TaoBaoConstant.APP_SECRET);
        AlibabaAliqinFcSmsNumSendRequest request=new AlibabaAliqinFcSmsNumSendRequest();
        request.setExtend("");
        request.setSmsType(TaoBaoConstant.SMS_TYPE);
        request.setSmsFreeSignName(TaoBaoConstant.SMS_FREE_SIGN_NAME);
        Map<String,String> map=new HashMap<>();
        map.put("code",code);
        request.setSmsParamString(JSON.toJSONString(map));
        request.setRecNum(phone);
        request.setSmsTemplateCode(TaoBaoConstant.SMS_TEMATE_CODE);
        AlibabaAliqinFcSmsNumSendResponse response=null;
        try {
            response=client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }
}
