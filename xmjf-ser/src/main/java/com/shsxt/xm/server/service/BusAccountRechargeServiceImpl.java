package com.shsxt.xm.server.service;

import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.query.AccountRechargeQuery;
import com.shsxt.xm.api.service.IBusAccountRechargeService;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BusAccountRechargeServiceImpl implements IBusAccountRechargeService {
    @Autowired
    private BusAccountRechargeDao busAccountRechargeDao;
    @Override
    public PageList queryRechargeRecodesByParams(AccountRechargeQuery accountRechargeQuery) {
        PageHelper.startPage(accountRechargeQuery.getPageNum(),accountRechargeQuery.getPageSize());
        List<BusAccountRecharge> busAccountRecharges = busAccountRechargeDao.queryForPage(accountRechargeQuery);
        return new PageList(busAccountRecharges);
    }
}
