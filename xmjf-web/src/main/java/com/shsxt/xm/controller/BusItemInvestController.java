package com.shsxt.xm.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.model.ResultInfo;
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

}
