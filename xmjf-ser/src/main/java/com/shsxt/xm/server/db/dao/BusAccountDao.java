package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.server.base.BaseDao;
import com.shsxt.xm.server.providers.BusAccountProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.math.BigDecimal;
import java.util.Map;

public interface BusAccountDao extends BaseDao<BusAccount> {
    /**
     * 查询登陆用户账户金额信息
     * @param userId
     * @return
     */
    @SelectProvider(type = BusAccountProvider.class,method = "getQueryBusAccountByUserIdSql")
    public BusAccount queryBusAccountByuserId(@Param("userId") Integer userId);

    public Map<String,BigDecimal> queryAccountInfoByUserId(@Param("userId") Integer userId);

}