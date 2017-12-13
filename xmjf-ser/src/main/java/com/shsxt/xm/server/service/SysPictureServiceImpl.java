package com.shsxt.xm.server.service;

import com.shsxt.xm.api.po.SysPicture;
import com.shsxt.xm.api.service.ISysPictureService;
import com.shsxt.xm.server.db.dao.SysPictureDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class SysPictureServiceImpl implements ISysPictureService {
    @Resource
    private SysPictureDao sysPictureDao;
    @Override
    public List<SysPicture> querySysPicturesByItemId(Integer itemId) {
        return sysPictureDao.querySysPicturesByItemId(itemId);
    }
}
