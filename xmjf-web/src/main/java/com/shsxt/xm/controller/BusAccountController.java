package com.shsxt.xm.controller;

import com.shsxt.xm.annotations.IsLogin;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.query.AccountRechargeQuery;
import com.shsxt.xm.api.service.IBusAccountRechargeService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.context.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("account")
public class BusAccountController extends BaseController{

    @Autowired
    private IBusAccountService busAccountService;

    @Resource
    private IBusAccountRechargeService busAccountRechargeService;



    @Autowired(required = false)
    private HttpSession session;


    /**
     * 转发到充值页面
     */
    @RequestMapping("rechargePage")
    public  String rechargePage(){
        return "user/recharge";
    }

    /**
     * 发起支付请求转发页面
     * @param amount
     * @param picCode
     * @param bussinessPassword
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("doAccountRechargeToRechargePage")
    public String doAccountRechargeToRechargePage(BigDecimal amount, String picCode, String bussinessPassword, HttpServletRequest request, Model model){
        String sessionPicCode= (String) request.getSession().getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        if(StringUtils.isBlank(sessionPicCode)){
            System.out.println("验证码已失效");
            return  "user/pay";
        }
        if(!picCode.equals(sessionPicCode)){
            System.out.println("验证码不匹配！");
            return  "user/pay";
        }
        BasUser basUser=(BasUser) request.getSession().getAttribute("user");
        PayDto payDto = busAccountService.addRechargeRequestInfo(amount, bussinessPassword, basUser.getId());
        model.addAttribute("pay",payDto);
        return "user/pay";
    }

    /**
     * 订单支付回掉
     * @param totalFee 充值金额
     * @param outOrderNo
     * @param sign 盐值
     * @param tradeNo 订单流水号
     * @param tradeStatus 订单状态
     * @param redirectAttributes springmvc 3.1版本后用与带参数重定向
     * @return
     */
    @RequestMapping("callback")
    public String callBack(@RequestParam(defaultValue = "0",name = "total_fee") BigDecimal totalFee,
                           @RequestParam(defaultValue = "1",name = "out_order_no") String outOrderNo,
                           String sign,
                           @RequestParam(defaultValue = "2017",name = "trade_no") String tradeNo,
                           @RequestParam(defaultValue = "success",name = "trade_status") String tradeStatus,
                           RedirectAttributes redirectAttributes){
        BasUser basUser =(BasUser)session.getAttribute("user");


        try {
            busAccountService.updateAccountRecharge(basUser.getId(),totalFee,outOrderNo,sign,tradeNo,tradeStatus);
            redirectAttributes.addAttribute("result","success");

        } catch (ParamsExcetion e) {
            e.printStackTrace();
            System.out.println(e.getErrorMsg());
            redirectAttributes.addAttribute("result","failed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("系统异常");
            redirectAttributes.addAttribute("result","failed");
        }

        return "redirect:/account/result";//重定向到controller接口
    }
    @RequestMapping("result")
    public String result(String result,Model model){
        model.addAttribute("result",result);
        return "result";
    }

    /**
     * 跳转到充值记录页面
     * @return
     */
    @RequestMapping("rechargeRecodePage")
    public String rechargeRecodePage(){
        return "user/recharge_record";
    }

    @RequestMapping("queryRechargeRecodesByUserId")
    @ResponseBody
    public PageList queryRechargeRecodesByUserId(){
        BasUser basUser=(BasUser)session.getAttribute("user");
        AccountRechargeQuery accountRechargeQuery=new AccountRechargeQuery();
        accountRechargeQuery.setUserId(basUser.getId());

        return busAccountRechargeService.queryRechargeRecodesByParams(accountRechargeQuery);
    }

    @IsLogin
    @RequestMapping("accountInfoPage")
    public String toAccountInfoPage(){
        return "user/account_info";
    }

    @RequestMapping("accountInfo")
    @ResponseBody
    public Map<String,Object> queryAccountInfoByUserId(){
        BasUser basUser=(BasUser)session.getAttribute("user");
        return busAccountService.queryAccountInfoByUserId(basUser.getId());
    }

}
