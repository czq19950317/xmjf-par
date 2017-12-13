package com.shsxt.xm.server.service;


import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.service.IBasItemService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.server.db.dao.BasItemDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BasItemServiceImpl implements IBasItemService{
    @Resource
    private BasItemDao basItemDao;
    @Override
    public PageList queryBasItemByParams(BasItemQuery basItemQuery) {
        PageHelper.startPage(basItemQuery.getPageNum(),basItemQuery.getPageSize());
        List<BasItemDto> basItemDtos = basItemDao.queryForPage(basItemQuery);
        if(!CollectionUtils.isEmpty(basItemDtos)){
            for (BasItemDto basItemDto:basItemDtos) {
                //如果记录处于带开放状态，则记录剩余时间
                if(basItemDto.getItemStatus().equals(1)){
                    Date relaseTime = basItemDto.getReleaseTime();

                    Long syTime=(relaseTime.getTime()-new Date().getTime())/1000;
                    basItemDto.setSyTime(syTime);
                }

            }
        }



        return new PageList(basItemDtos);
    }

    @Override
    public void updateBasItemStatusToOpen(Integer itemId) {
        AssertUtil.isTrue(null==basItemDao.queryById(itemId),"待更新记录不存在");
        AssertUtil.isTrue(basItemDao.updateBasItemStatusToOpen(itemId)<1, P2PConstant.OPS_FAILED_MSG);
    }

    /**
     * 根据id查询对应的记录
     * @param itemId
     * @return
     */
    @Override
    public BasItemDto queryBasItemByItemId(Integer itemId) {
        return basItemDao.queryById(itemId);
    }
}
