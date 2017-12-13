package com.shsxt.xm.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.service.*;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.context.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("basItem")
public class BasItemController extends BaseController{

    @Resource
    private IBasItemService basItemService;
    @Autowired
    private IBusAccountService busAccountService;

    @Autowired
    private IBasUserSecurityService basUserSecurityService;
    @Autowired
    private IBusItemLoanService busItemLoanService;
    @Autowired
    private ISysPictureService sysPictureService;
    @RequestMapping("list")
    public String toBasItemListPage(){
       return "item/invest_list";
    }

    @RequestMapping("queryBasItemsByParams")
    @ResponseBody
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery){
        return basItemService.queryBasItemByParams(basItemQuery);
    }

    @ResponseBody
    @RequestMapping("updateBasItemStatusToOpen")
    public ResultInfo updateBasItemStatusToOpen(Integer itemId){
        ResultInfo resultInfo=new ResultInfo();
        try {
            basItemService.updateBasItemStatusToOpen(itemId);
        }catch (ParamsExcetion e){

            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }

        return resultInfo;
    }

    @RequestMapping("itemDetailPage")
    public  String itemDetailPage(Integer itemId , ModelMap modelMap,HttpServletRequest request){

        BasItemDto basItemDto = basItemService.queryBasItemByItemId(itemId);
        BasUser basUser=(BasUser) request.getSession().getAttribute("user");
        if(null!=basUser){
            BusAccount busAccount = busAccountService.queryBusAccountByUserId(basUser.getId());
            modelMap.addAttribute("busAccount",busAccount);
        }
        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(basItemDto.getItemUserId());
        BusItemLoan busItemLoan = busItemLoanService.queryBusItemLoanByItemId(itemId);
        List<SysPicture> sysPictures = sysPictureService.querySysPicturesByItemId(itemId);
        modelMap.addAttribute("loanUser",basUserSecurity);
        modelMap.addAttribute("busItemLoan",busItemLoan);
        modelMap.addAttribute("pics",sysPictures);

        modelMap.addAttribute("item",basItemDto);
        return "item/details";
    }

}
