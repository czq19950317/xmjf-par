package com.shsxt.xm.server.service;

import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.MD5;
import com.shsxt.xm.server.db.dao.BasUserSecurityDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BasUserSecurityServiceImpl implements IBasUserSecurityService {
  @Resource
   private BasUserSecurityDao basUserSecurityDao;
  @Autowired
  private IBasUserService basUserService;
    @Override
    public BasUserSecurity queryBasUserSecurityByUserId(Integer userId) {
        return basUserSecurityDao.queryBasUserSecurityByUserId(userId);
    }

    /**
     * 用户认证校验
     * @param userId
     * @return
     */
    @Override
    public ResultInfo userAuthCheck(Integer userId) {
        ResultInfo resultInfo=new ResultInfo();
        BasUserSecurity basUserSecurity = basUserSecurityDao.queryBasUserSecurityByUserId(userId);
        if(basUserSecurity.getRealnameStatus().equals(0)){
            /**
             * 用户未认证
              */
            resultInfo.setCode(301);
            resultInfo.setMsg("用户未进行实名认证");
        }
        if(basUserSecurity.getRealnameStatus().equals(1)){
            /**
             * 用户已经认证
             */
            resultInfo.setCode(200);
            resultInfo.setMsg("改用户已认证");
        }
        if(basUserSecurity.getRealnameStatus().equals(2)){
            /**
             * 用户已提交认证申请
             */
            resultInfo.setCode(302);
            resultInfo.setMsg("认证申请已提交，正在认证中");
        }
        return resultInfo;
    }

    /**
     * 认证用户
     * @param realName
     * @param idCard
     * @param businessPassword
     * @param confirmPassword
     * @param userId
     */
    @Override
    public void doUserAuth(String realName, String idCard, String businessPassword, String confirmPassword, Integer userId) {
        AssertUtil.isTrue(null==userId||null==basUserService.queryBasUserzById(userId),"用户不存在或者未登录");
        AssertUtil.isTrue(StringUtils.isBlank(idCard),"身份证号不能为空");
        AssertUtil.isTrue(idCard.length()!=18,"身份证号格式错误");
        AssertUtil.isTrue(StringUtils.isBlank(businessPassword)||StringUtils.isBlank(confirmPassword)||!businessPassword.equals(confirmPassword),"密码错误");
        AssertUtil.isTrue(null!=basUserSecurityDao.queryBasUserSecurityByIdCard(idCard),"该身份证已被认证");
        BasUserSecurity basUserSecurity = basUserSecurityDao.queryBasUserSecurityByUserId(userId);
        basUserSecurity.setRealname(realName);
        basUserSecurity.setPaymentPassword(MD5.toMD5(businessPassword));
        basUserSecurity.setIdentifyCard(idCard);
        basUserSecurity.setRealnameStatus(1);
        AssertUtil.isTrue(basUserSecurityDao.update(basUserSecurity)<1,"认证失败");

    }
}
