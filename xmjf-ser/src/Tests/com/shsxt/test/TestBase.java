package com.shsxt.test;

import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.po.BasItem;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.server.db.dao.BasItemDao;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class TestBase {

    @Resource
    private BasItemDao basItemDao;
    @org.junit.Test
    public void test(){
        BasItemQuery basItemQuery=new BasItemQuery();
        basItemQuery.setItemCycle(2);
        basItemQuery.setIsHistory(1);
        //basItemQuery.setItemType(2);

        List<BasItemDto> basItems = basItemDao.queryForPage(basItemQuery);
        if(!CollectionUtils.isEmpty(basItems)){
            for (BasItemDto itemDto:basItems) {
                System.out.println(itemDto);
            }
        }else {
            System.out.println("暂无记录");
        }
    }
}
