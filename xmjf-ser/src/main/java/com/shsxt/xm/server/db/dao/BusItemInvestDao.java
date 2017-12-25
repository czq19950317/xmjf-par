package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.dto.InvestDto;
import com.shsxt.xm.api.po.BusItemInvest;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusItemInvestDao extends BaseDao<BusItemInvest>{
    /**
     * 查询用户是否投资过新手标
     */
    public Integer queryUserIsInvestIsNewItem(@Param("userId") Integer userId);

    public List<InvestDto> queryInvestInfoByUserId(@Param("userId")Integer userId);

}