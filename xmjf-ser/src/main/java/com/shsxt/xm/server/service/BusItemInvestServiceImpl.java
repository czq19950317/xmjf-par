package com.shsxt.xm.server.service;

import com.alibaba.dubbo.container.page.PageHandler;
import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.dto.InvestDto;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.IBusItemInvestService;
import com.shsxt.xm.api.utils.*;
import com.shsxt.xm.server.db.dao.*;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusItemInvestServiceImpl implements IBusItemInvestService{

    @Resource
    private BusItemInvestDao busItemInvestDao;
    @Resource
    private IBasUserService basUserService;
    @Resource
    private BasItemDao basItemDao;
    @Resource
    private BusAccountDao busAccountDao;
    @Resource
    private BusAccountLogDao busAccountLogDao;
    @Resource
    private IBasUserSecurityService basUserSecurityService;
    @Resource
    private BusUserStatDao busUserStatDao;
    @Resource
    private BusUserIntegralDao busUserIntegralDao;
    @Resource
    private BusIncomeStatDao busIncomeStatDao;

    @Resource
    private BusIntegralLogDao busIntegralLogDao;

    @Override
    public PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery) {
        PageHelper.startPage(busItemInvestQuery.getPageNum(),busItemInvestQuery.getPageSize());
        List<BusItemInvest> busItemInvests = busItemInvestDao.queryForPage(busItemInvestQuery);
        return new PageList(busItemInvests);
    }

    /**
     * 用户投资方法
     * @param userId
     * @param itemId
     * @param amount
     * @param businessPassword
     */
    @Override
    public void addBusItemInvest(Integer userId, Integer itemId, BigDecimal amount, String businessPassword) {
        //参数的基本校验
        checkInvestParams(userId,itemId,amount,businessPassword);
        BusItemInvest busItemInvest=new BusItemInvest();
        busItemInvest.setActualCollectAmount(BigDecimal.ZERO);
        busItemInvest.setActualCollectInterest(BigDecimal.ZERO);
        busItemInvest.setActualCollectPrincipal(BigDecimal.ZERO);
        //实际未收总额 本金+利息
        BasItem basItem=basItemDao.queryById(itemId);
        BigDecimal lx= Calculator.getInterest(amount,basItem);
        busItemInvest.setActualCollectAmount(amount.add(lx));
        busItemInvest.setActualCollectInterest(lx);
        busItemInvest.setActualCollectPrincipal(amount);
        busItemInvest.setAdditionalRateAmount(BigDecimal.ZERO);
        busItemInvest.setAddtime(new Date());
        busItemInvest.setCollectInterest(lx);
        busItemInvest.setCollectAmount(amount.add(lx));
        busItemInvest.setCollectPrincipal(amount);
        busItemInvest.setInvestAmount(amount);
        busItemInvest.setInvestCurrent(1);
        busItemInvest.setInvestDealAmount(amount);
        String oderNo="SXT_TZ_"+ RandomCodesUtils.createRandom(false,11);
        busItemInvest.setInvestOrder(oderNo);
        busItemInvest.setInvestStatus(0);
        busItemInvest.setInvestType(1);//pc端投资
        busItemInvest.setItemId(itemId);
        busItemInvest.setUserId(userId);
        AssertUtil.isTrue(busItemInvestDao.insert(busItemInvest)<1, P2PConstant.OPS_FAILED_MSG);

        BusUserStat busUserStat=busUserStatDao.queryBusUserStatByUserId(userId);
        busUserStat.setInvestCount(busUserStat.getInvestCount()+1);
        busUserStat.setInvestAmount(busUserStat.getInvestLaveAmount().add(amount));
        AssertUtil.isTrue(busUserStatDao.update(busUserStat)<1,P2PConstant.OPS_FAILED_MSG);

        BusAccount busAccount=busAccountDao.queryBusAccountByuserId(userId);
        busAccount.setTotal(busAccount.getTotal().add(lx));
        busAccount.setUsable(busAccount.getUsable().add(amount.negate()));
        busAccount.setCash(busAccount.getCash().add(amount.negate()));
        busAccount.setFrozen(busAccount.getFrozen().add(amount));
        busAccount.setWait(busAccount.getWait().add(amount));
        AssertUtil.isTrue(busAccountDao.update(busAccount)<1,P2PConstant.OPS_FAILED_MSG);

        BusAccountLog busAccountLog=new BusAccountLog();
        busAccountLog.setUserId(userId);
        busAccountLog.setOperType("用户投标");
        busAccountLog.setOperMoney(amount);
        busAccountLog.setBudgetType(2);
        busAccountLog.setTotal(busAccount.getTotal());
        busAccountLog.setUsable(busAccount.getUsable());
        busAccountLog.setFrozen(busAccount.getFrozen());
        busAccountLog.setWait(busAccount.getWait());
        busAccountLog.setCash(busAccount.getCash());
        busAccountLog.setRepay(busAccount.getRepay());
        busAccountLog.setRemark("用户投标成功！");
        busAccountLog.setAddtime(new Date());
        AssertUtil.isTrue(busAccountLogDao.insert(busAccountLog)<1,P2PConstant.OPS_FAILED_MSG);

        BusIncomeStat busIncomeStat=busIncomeStatDao.queryBusIncomeStatByUserId(userId);
        busIncomeStat.setWaitIncome(busIncomeStat.getWaitIncome().add(lx));
        busIncomeStat.setTotalIncome(busIncomeStat.getTotalIncome().add(lx));
        AssertUtil.isTrue(busIncomeStatDao.update(busIncomeStat)<1,P2PConstant.OPS_FAILED_MSG);

        BusUserIntegral busUserIntegral=busUserIntegralDao.queryBusUserIntegralByUserId(userId);
        busUserIntegral.setTotal(busUserIntegral.getTotal()+100);
        busUserIntegral.setUsable(busUserIntegral.getUsable()+100);
        AssertUtil.isTrue(busUserIntegralDao.update(busUserIntegral)<1,P2PConstant.OPS_FAILED_MSG);

        BusIntegralLog busIntegralLog=new BusIntegralLog();
        busIntegralLog.setWay("用户投标");
        busIntegralLog.setUserId(userId);
        busIntegralLog.setStatus(0);
        busIntegralLog.setAddtime(new Date());
        AssertUtil.isTrue(busIntegralLogDao.insert(busIntegralLog)<1,P2PConstant.OPS_FAILED_MSG);
        //更新贷款项目信息
        basItem.setItemOngoingAccount(basItem.getItemOngoingAccount().add(amount));
        basItem.setInvestTimes(basItem.getInvestTimes()+1);
        if(basItem.getItemAccount().compareTo(basItem.getItemOngoingAccount())==0){
            basItem.setItemStatus(ItemStatus.FULL_COMPLETE);
        }

        MathContext mc=new MathContext(2, RoundingMode.HALF_DOWN);
        basItem.setItemScale(basItem.getItemOngoingAccount().divide(basItem.getItemAccount(),mc).multiply(BigDecimal.valueOf(100)));
        AssertUtil.isTrue(basItemDao.update((BasItemDto) basItem)<1,P2PConstant.OPS_FAILED_MSG);



    }

    @Override
    public Map<String, Object[]> queryInvestInfoByUserId(Integer userId) {
        Map<String,Object[]> map=new HashMap<>();
        List<InvestDto> list=busItemInvestDao.queryInvestInfoByUserId(userId);
        String[] month;//存放月份
        BigDecimal[] totals;
        if(!CollectionUtils.isEmpty(list)){
            month=new String[list.size()];
            totals=new BigDecimal[list.size()];
            for (int i=0;i<list.size();i++){
                InvestDto investDto = list.get(i);
                month[i]=investDto.getMonth();
                totals[i]=investDto.getTotal();

            }
            map.put("data1",month);
            map.put("data2",totals);
        }
        return map;
    }

    /**
     * 1.
     * @param userId
     * @param itemId
     * @param amount
     * @param businessPassword
     */
    private void checkInvestParams(Integer userId, Integer itemId, BigDecimal amount, String businessPassword) {

        //用户是否登录校验
        AssertUtil.isTrue(null==userId||userId==0||null==basUserService.queryBasUserzById(userId),"用户未登录或不存在该用户");

        //交易密码校验
        AssertUtil.isTrue(StringUtils.isBlank(businessPassword),"交易密码不能为空");
        BasUserSecurity basUserSecurity=basUserSecurityService.queryBasUserSecurityByUserId(userId);
        businessPassword= MD5.toMD5(businessPassword);
        AssertUtil.isTrue(!businessPassword.equals(basUserSecurity.getPaymentPassword()),"交易密码不正确");
                //投资项目存在性校验
        BasItem basItem=basItemDao.queryById(itemId);
        AssertUtil.isTrue(null==itemId||basItem==null,"投资项目不存在");

        //是否为移动端校验(仅限非移动端)
        AssertUtil.isTrue(basItem.getMoveVip().equals(1),"移动端专项项目，pc端不能进行投资");
        //投资项目开放性校验
        AssertUtil.isTrue(!basItem.getItemStatus().equals(10),"该项目处于未开放状态，不能进行投资");
                ///投资项目是否满标校验
        AssertUtil.isTrue(basItem.getItemStatus().equals(20),"项目已满标，不能进行投资处理");
        //账户金额合法性校验
        BusAccount busAccount = busAccountDao.queryBusAccountByuserId(userId);
        AssertUtil.isTrue(busAccount.getUsable().compareTo(BigDecimal.ZERO)<=0,"账户金额不足，请先充值");


        //投资金额合法性校验
        AssertUtil.isTrue(null==amount||amount.compareTo(BigDecimal.ZERO)<=0,"投资金额非法");
        //小于账户可用余额
        
        BigDecimal singleMinInvestment = basItem.getItemSingleMinInvestment();
        //余额=项目总金额-进行中余额
        BigDecimal syAmount=basItem.getItemAccount().add(basItem.getItemOngoingAccount().negate());// BigDecimal.negate取负数
        if(singleMinInvestment.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(syAmount.compareTo(singleMinInvestment)<0,"项目已满标");
            AssertUtil.isTrue(amount.compareTo(singleMinInvestment)<0,"单笔投资不能小于最小投资项目");
        }


                //项目剩余金额合法性校验
        //最小投资金额存在
        //剩余金额>最小投资金额
        if(singleMinInvestment.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(busAccount.getUsable().compareTo(singleMinInvestment)<0,"账户余额小于当前项目最小投资金额，请先进行充值操作");
        }
        
       // 投资金额>=最大投资金额
        BigDecimal singleMaxInvestment = basItem.getItemSingleMaxInvestment();
        if(singleMaxInvestment.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(amount.compareTo(singleMaxInvestment)>0,"单笔投资不能大于最大投资金额");
        }
        //  新手标重复投资记录校验
        AssertUtil.isTrue(busItemInvestDao.queryUserIsInvestIsNewItem(userId)>0,"新手标不能进行重复投资操作");


    }
}
