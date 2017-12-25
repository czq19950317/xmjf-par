package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusAccountRechargeDao extends BaseDao<BusAccountRecharge> {
    public BusAccountRecharge queryBusAccountRechargeByOrderNo(@Param("orderNo") String orderNo);
}