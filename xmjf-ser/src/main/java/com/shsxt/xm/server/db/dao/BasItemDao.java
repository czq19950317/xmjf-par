package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.po.BasItem;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BasItemDao extends BaseDao<BasItemDto> {

    public Integer updateBasItemStatusToOpen(@Param("itemId") Integer itemId);
}