package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.MD5;
import com.shsxt.xm.server.db.dao.BusAccountDao;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import com.shsxt.xm.server.utils.Md5Util;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class BusAccountServiceImpl implements IBusAccountService {
    @Autowired
    private BusAccountDao busAccountDao;
    @Resource
    private IBasUserSecurityService basUserSecurityService;
    @Resource
    private BusAccountRechargeDao busAccountRechargeDao;
    @Override
    public BusAccount queryBusAccountByUserId(Integer userId) {
        return busAccountDao.queryBusAccountByuserId(userId);
    }

    /**
     * 支付订单服务
     * @param amount
     * @param password
     * @param userId
     * @return
     */
    @Override
    public PayDto addRechargeRequestInfo(BigDecimal amount, String password, Integer userId) {
        checkParams(amount,password,userId);
        /**
         * 构建支付请求参数信息
         */
        BusAccountRecharge busAccountRecharge=new BusAccountRecharge();
        busAccountRecharge.setAddtime(new Date());
        busAccountRecharge.setFeeAmount(BigDecimal.ZERO);
        busAccountRecharge.setFeeRate(BigDecimal.ZERO);
        String orderNo= com.shsxt.xm.server.utils.StringUtils.getOrderNo();
        busAccountRecharge.setOrderNo(orderNo);
        busAccountRecharge.setRechargeAmount(amount);
        busAccountRecharge.setRemark("pc端用户充值");
        busAccountRecharge.setResource("pc端用户充值");
        busAccountRecharge.setStatus(2);
        busAccountRecharge.setType(3);
        busAccountRecharge.setUserId(userId);
        AssertUtil.isTrue(busAccountRechargeDao.insert(busAccountRecharge)<1, P2PConstant.OPS_FAILED_MSG);
        PayDto payDto=new PayDto();
        payDto.setBody("pc端用户充值操作");
        payDto.setOrderNo(orderNo);
        payDto.setSubject("pc端用户充值操作");
        payDto.setTotalFee(amount);
        String md5Sign=buildMd5Sign(payDto);
        payDto.setSign(md5Sign);

        return payDto;
    }

    private String buildMd5Sign(PayDto payDto) {
        StringBuffer arg=new StringBuffer();
        if(!StringUtils.isBlank(payDto.getBody())){
            arg.append("body="+payDto.getBody()+"&");
        }
        arg.append("notify_url="+payDto.getNotifyUrl()+"&");
        arg.append("out_order_no="+payDto.getOrderNo()+"&");
        arg.append("partner="+payDto.getPartner()+"&");
        arg.append("return_url="+payDto.getReturnUrl()+"&");
        arg.append("subject="+payDto.getSubject()+"&");
        arg.append("total_fee="+payDto.getTotalFee().toString()+"&");
        arg.append("user_seller="+payDto.getUserSeller());
        String tempSign= StringEscapeUtils.unescapeJava(arg.toString());
        Md5Util md5Util=new Md5Util();
        return md5Util.encode(tempSign+payDto.getKey(),"");
    }

    /**
     * 基本参数校验
     * @param amount
     * @param password
     * @param userId
     */
    private void checkParams(BigDecimal amount, String password, Integer userId) {
        AssertUtil.isTrue(amount.compareTo(BigDecimal.ZERO)<=0,"充值金额非法");
        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(userId);
        AssertUtil.isTrue(null==basUserSecurity,"用户未登录");
        AssertUtil.isTrue(StringUtils.isBlank(password),"交易密码不能为空");
        password= MD5.toMD5(password);
        AssertUtil.isTrue(!password.equals(basUserSecurity.getPaymentPassword()),"交易密码错误");
    }
}
