package com.shsxt.xm.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.service.IBusItemInvestService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.context.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("busItemInvest")
public class BusItemInvestController extends BaseController {

    @Resource
    private IBusItemInvestService busItemInvestService;

    @PostMapping("queryBusItemInvestsByItemId")
    @ResponseBody
    public ResultInfo queryBusItemInvestsByItemId(BusItemInvestQuery busItemInvestQuery){
        ResultInfo resultInfo=new ResultInfo();

        PageList busItemInvests = busItemInvestService.queryBusItemInvestsByParams(busItemInvestQuery);
        AssertUtil.isTrue(busItemInvests==null,P2PConstant.OPS_FAILED_MSG);

        resultInfo.setResult(busItemInvests);

        return resultInfo;
    }

    @RequestMapping("userInvest")
    @ResponseBody
    public ResultInfo userInvest(Integer itemId, BigDecimal amount, String businessPassword, HttpSession session){
        ResultInfo resultInfo=new ResultInfo();
        try {
            BasUser basUser=(BasUser)session.getAttribute("user");
            busItemInvestService.addBusItemInvest(basUser.getId(),itemId,amount,businessPassword);
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setMsg(e.getErrorMsg());
            resultInfo.setCode(e.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
        }
        return resultInfo;
    }

    @ResponseBody
    @RequestMapping("queryInvestInfoByUserId")
    public Map<String,Object[]> queryInvestInfoByUserId(HttpSession session){
        BasUser basUser=(BasUser)session.getAttribute("user");
        return busItemInvestService.queryInvestInfoByUserId(basUser.getId());
    }

}
