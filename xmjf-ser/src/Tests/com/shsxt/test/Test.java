package com.shsxt.test;

import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.ISmsService;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


public class Test extends TestBase{
    @Resource
    private IBasUserService userService;

    @Resource
    private ISmsService smsService;
    @org.junit.Test
    public void test(){
        BasUser user = userService.queryBasUserzById(1);
        System.out.println(user);
    }
    @org.junit.Test
    public void test2(){
        smsService.sendPhoneSms("18661197721","nnnn",1);
    }
    @org.junit.Test
    public void test3(){
        smsService.sendPhoneSms("18661197721","nnnn",1);
    }
}
