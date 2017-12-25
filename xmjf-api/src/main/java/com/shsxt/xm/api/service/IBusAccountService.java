package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BusAccount;

import java.math.BigDecimal;
import java.util.Map;

public interface IBusAccountService {

    public BusAccount queryBusAccountByUserId(Integer userId);
    //支付订单服务
    public PayDto addRechargeRequestInfo(BigDecimal amount, String password,Integer userId);

    /**
     * 更新支付订单信息
     * @param userId
     * @param totalFee
     * @param outOrderNo
     * @param sign
     * @param tradeNo
     * @param tradeStatus
     */
    public  void updateAccountRecharge(Integer userId, BigDecimal totalFee,String outOrderNo,String sign, String  tradeNo, String  tradeStatus);

    /**
     * 资产详情
     */
    public Map<String,Object> queryAccountInfoByUserId(Integer userId);

}
