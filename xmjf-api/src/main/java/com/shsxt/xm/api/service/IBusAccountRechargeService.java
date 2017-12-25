package com.shsxt.xm.api.service;

import com.shsxt.xm.api.query.AccountRechargeQuery;
import com.shsxt.xm.api.utils.PageList;

public interface IBusAccountRechargeService {
    public PageList queryRechargeRecodesByParams(AccountRechargeQuery accountRechargeQuery);
}
