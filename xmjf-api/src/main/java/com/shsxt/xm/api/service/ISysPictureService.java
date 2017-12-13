package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.SysPicture;

import java.util.List;

public interface ISysPictureService {
    public List<SysPicture> querySysPicturesByItemId(Integer itemId);
}
