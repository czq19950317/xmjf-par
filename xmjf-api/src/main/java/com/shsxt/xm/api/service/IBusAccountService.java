package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BusAccount;

import java.math.BigDecimal;

public interface IBusAccountService {

    public BusAccount queryBusAccountByUserId(Integer userId);
    //支付订单服务
    public PayDto addRechargeRequestInfo(BigDecimal amount, String password,Integer userId);
}
