package com.shsxt.xm.api.service;

import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.utils.PageList;

public interface IBusItemInvestService {
    public PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery);
}
