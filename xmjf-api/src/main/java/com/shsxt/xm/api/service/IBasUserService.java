package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.BasUser;

public interface IBasUserService {
    public BasUser queryBasUserzById(Integer id);

    /**
     * 通过手机查询用户
     * @param phone
     * @return
     */
    public BasUser queryBasUserByPhone(String phone);

    /**
     * 保存用户记录
     * @param phone
     * @param passworld
     */
    public void saveBasUser(String phone,String passworld);

    /**
     * 手机号+密码登陆
     * @param phone
     * @param password
     * @return
     */
    public BasUser userLogin(String phone,String password);
    /**
     * 快捷登陆
     */
    public BasUser quickLogin(String phone);


}
