package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.utils.PageList;

public interface IBasItemService {
    public PageList queryBasItemByParams(BasItemQuery basItemQuery);

    /**
     * 倒计时项目刷新成可投标项目
     * @param itemId
     */
    public void updateBasItemStatusToOpen(Integer itemId);

    public BasItemDto queryBasItemByItemId(Integer itemId);
}
