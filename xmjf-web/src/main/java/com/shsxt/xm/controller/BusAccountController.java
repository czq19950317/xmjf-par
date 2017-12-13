package com.shsxt.xm.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.context.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("account")
public class BusAccountController extends BaseController{

    @Autowired
    private IBusAccountService busAccountService;

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

    @RequestMapping("callback")
    public String callBack(){
        return "";
    }

}
