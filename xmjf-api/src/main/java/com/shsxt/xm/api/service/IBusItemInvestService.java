package com.shsxt.xm.api.service;

import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.utils.PageList;

import java.math.BigDecimal;
import java.util.Map;

public interface IBusItemInvestService {
    public PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery);

    /**
     * 用户投资方法
     */
    public void addBusItemInvest(Integer userId, Integer itemId, BigDecimal amount, String businessPassword);

    public Map<String,Object[]> queryInvestInfoByUserId(Integer userId);
}
