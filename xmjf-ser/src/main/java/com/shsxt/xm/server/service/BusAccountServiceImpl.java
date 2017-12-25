package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.constant.YunTongFuConstant;
import com.shsxt.xm.api.dto.AccountDto;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.api.po.BusAccountLog;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.MD5;
import com.shsxt.xm.server.db.dao.BusAccountDao;
import com.shsxt.xm.server.db.dao.BusAccountLogDao;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import com.shsxt.xm.server.utils.Md5Util;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class BusAccountServiceImpl implements IBusAccountService {
    @Resource
    private BusAccountLogDao busAccountLogDao;
    @Autowired
    private BusAccountDao busAccountDao;
    @Resource
    private IBasUserService basuserService;
    @Resource
    private IBasUserSecurityService basUserSecurityService;
    @Resource
    private BusAccountRechargeDao busAccountRechargeDao;
    @Override
    public BusAccount queryBusAccountByUserId(Integer userId) {
        return busAccountDao.queryBusAccountByuserId(userId);
    }

    /**
     * 支付订单服务
     * @param amount
     * @param password
     * @param userId
     * @return
     */
    @Override
    public PayDto addRechargeRequestInfo(BigDecimal amount, String password, Integer userId) {
        checkParams(amount,password,userId);
        /**
         * 构建支付请求参数信息
         */
        BusAccountRecharge busAccountRecharge=new BusAccountRecharge();
        busAccountRecharge.setAddtime(new Date());
        busAccountRecharge.setFeeAmount(BigDecimal.ZERO);
        busAccountRecharge.setFeeRate(BigDecimal.ZERO);
        String orderNo= com.shsxt.xm.server.utils.StringUtils.getOrderNo();
        busAccountRecharge.setOrderNo(orderNo);
        busAccountRecharge.setRechargeAmount(amount);
        busAccountRecharge.setRemark("pc端用户充值");
        busAccountRecharge.setResource("pc端用户充值");
        busAccountRecharge.setStatus(2);
        busAccountRecharge.setType(3);
        busAccountRecharge.setUserId(userId);
        AssertUtil.isTrue(busAccountRechargeDao.insert(busAccountRecharge)<1, P2PConstant.OPS_FAILED_MSG);
        PayDto payDto=new PayDto();
        payDto.setBody("pc端用户充值操作");
        payDto.setOrderNo(orderNo);
        payDto.setSubject("pc端用户充值操作");
        payDto.setTotalFee(amount);
        String md5Sign=buildMd5Sign(payDto);
        payDto.setSign(md5Sign);

        return payDto;
    }

    /**
     * 支付完毕，更新支付订单信息
     * @param userId
     * @param totalFee
     * @param outOrderNo
     * @param sign
     * @param tradeNo
     * @param tradeStatus
     */
    @Override
    public void updateAccountRecharge(Integer userId, BigDecimal totalFee, String outOrderNo, String sign, String tradeNo, String tradeStatus) {
        AssertUtil.isTrue(null==userId||null==basuserService.queryBasUserzById(userId),"用户未登录");
        AssertUtil.isTrue(null==totalFee||StringUtils.isBlank(outOrderNo)||StringUtils.isBlank(sign)||StringUtils.isBlank(tradeNo)||StringUtils.isBlank(tradeStatus),
                "回掉参数异常");
        Md5Util md5Util=new Md5Util();
        String tempStr=md5Util.encode(outOrderNo+totalFee+tradeStatus+ YunTongFuConstant.PARTNER+YunTongFuConstant.KEY,null);
        AssertUtil.isTrue(!tempStr.equals(sign),"订单信息异常，请联系客服");
        AssertUtil.isTrue(!tradeStatus.equals(YunTongFuConstant.TRADE_STATUS_SUCCESS),"订单支付失败");
        BusAccountRecharge busAccountRecharge = busAccountRechargeDao.queryBusAccountRechargeByOrderNo(outOrderNo);
        AssertUtil.isTrue(busAccountRecharge==null,"订单记录不存在，请联系管理员");
        AssertUtil.isTrue(busAccountRecharge.getStatus().equals(1),"该订单已支付");
        AssertUtil.isTrue(busAccountRecharge.getStatus().equals(0),"订单异常，请联系客服");
        AssertUtil.isTrue(busAccountRecharge.getRechargeAmount().compareTo(totalFee)!=0,"订单异常，请联系客服");
        busAccountRecharge.setStatus(1);
        busAccountRecharge.setActualAmount(totalFee);
        busAccountRecharge.setAuditTime(new Date());
        AssertUtil.isTrue(busAccountRechargeDao.update(busAccountRecharge)<1,P2PConstant.OPS_FAILED_MSG);
        BusAccount busAccount = busAccountDao.queryBusAccountByuserId(userId);
        busAccount.setCash(busAccount.getCash().add(totalFee));//设置可提现金额
        busAccount.setTotal(busAccount.getTotal().add(totalFee));//设置总金额
        busAccount.setUsable(busAccount.getUsable().add(totalFee));//设置可用金额
        AssertUtil.isTrue(busAccountDao.update(busAccount)<1,P2PConstant.OPS_FAILED_MSG);
        //添加订单日志
        BusAccountLog busAccountLog=new BusAccountLog();
        busAccountLog.setAddtime(new Date());
        busAccountLog.setBudgetType(1);//收入日志
        busAccountLog.setCash(totalFee);//设置可提现金额
        busAccountLog.setFrozen(busAccount.getFrozen());
        busAccountLog.setOperMoney(totalFee);//设置操作金额
        busAccountLog.setOperType("user_recharge_success");
        busAccountLog.setRemark("用户充值");
        busAccountLog.setRepay(busAccount.getRepay());
        busAccountLog.setTotal(busAccount.getTotal());
        busAccountLog.setUsable(busAccount.getUsable());
        busAccountLog.setUserId(userId);
        busAccountLog.setWait(busAccount.getWait());
        AssertUtil.isTrue(busAccountLogDao.insert(busAccountLog)<1,P2PConstant.OPS_FAILED_MSG);
    }

    /**
     * 资产详情
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> queryAccountInfoByUserId(Integer userId) {
        Map<String, BigDecimal> map = busAccountDao.queryAccountInfoByUserId(userId);
        List<AccountDto> list=new ArrayList<AccountDto>();
        Map<String,Object> target=new HashMap<String,Object>();
        if(null!=map&&!map.isEmpty()){
            Set<Map.Entry<String, BigDecimal>> entries = map.entrySet();
            for(Map.Entry<String, BigDecimal> entry : entries){
                AccountDto accountDto=new AccountDto();
                accountDto.setName(entry.getKey());
                accountDto.setY(entry.getValue());
                list.add(accountDto);
                if(entry.getKey().equals("总金额")){
                    target.put("data2",entry.getValue());
                }
            }
            target.put("data1",list);
        }
        return target;
    }

    private String buildMd5Sign(PayDto payDto) {
        StringBuffer arg=new StringBuffer();
        if(!StringUtils.isBlank(payDto.getBody())){
            arg.append("body="+payDto.getBody()+"&");
        }
        arg.append("notify_url="+payDto.getNotifyUrl()+"&");
        arg.append("out_order_no="+payDto.getOrderNo()+"&");
        arg.append("partner="+payDto.getPartner()+"&");
        arg.append("return_url="+payDto.getReturnUrl()+"&");
        arg.append("subject="+payDto.getSubject()+"&");
        arg.append("total_fee="+payDto.getTotalFee().toString()+"&");
        arg.append("user_seller="+payDto.getUserSeller());
        String tempSign= StringEscapeUtils.unescapeJava(arg.toString());
        Md5Util md5Util=new Md5Util();
        return md5Util.encode(tempSign+payDto.getKey(),"");
    }

    /**
     * 基本参数校验
     * @param amount
     * @param password
     * @param userId
     */
    private void checkParams(BigDecimal amount, String password, Integer userId) {
        AssertUtil.isTrue(amount.compareTo(BigDecimal.ZERO)<=0,"充值金额非法");
        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(userId);
        AssertUtil.isTrue(null==basUserSecurity,"用户未登录");
        AssertUtil.isTrue(StringUtils.isBlank(password),"交易密码不能为空");
        password= MD5.toMD5(password);
        AssertUtil.isTrue(!password.equals(basUserSecurity.getPaymentPassword()),"交易密码错误");
    }
}
