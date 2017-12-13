package com.shsxt.xm.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.context.BaseController;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;


@Controller
@RequestMapping("user")
public class BasUserController extends BaseController{
    @Resource
   private IBasUserService basUserService;
    @Autowired
    private IBasUserSecurityService basUserSecurityService;
    @Autowired(required = false)
    private  HttpSession session;
    @ResponseBody
    @RequestMapping("user")
    public BasUser queryBasUserById(Integer id){
        return  basUserService.queryBasUserzById(id);
    }

    /**
     * 注册用户
     * @return
     */
    @RequestMapping("register")
    @ResponseBody
    public ResultInfo saveBasUser(String phone, String picCode, String code, String password){
        ResultInfo resultInfo=new ResultInfo();
        //获取session中图片验证码的值
        String sessionPicCode=(String) session.getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        /**
         * 验证验证码是否过期
         */
        if(StringUtils.isBlank(sessionPicCode)){
            resultInfo.setMsg("验证码已失效");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        /**
         * 验证验证码是否一致
         */
        if(!picCode.equals(sessionPicCode)){
            resultInfo.setMsg("验证码不一致");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        /**
         * 获得发送验证码的时间
         */
        Date sessionTime=(Date) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
        if(null==sessionTime){
            resultInfo.setMsg("手机验证码已失效");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        Date currTime=new Date();
        //查看是否超时
        long time=(currTime.getTime()-sessionTime.getTime())/1000;
        if(time>180){
            resultInfo.setMsg("手机验证码已失效");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        //获取手机验证码
        String sessionCode=(String)session.getAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
        if(!sessionCode.equals(code)){
            resultInfo.setMsg("手机验证码不一致");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }

        try {
            basUserService.saveBasUser(phone,password);
            //移除session中储存的key信息
            session.removeAttribute(P2PConstant.PICTURE_VERIFY_CODE);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
        }catch (ParamsExcetion e){
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }

    /**
     * 用户账号密码登陆
     * @param phone
     * @param password
     * @return
     */
    @ResponseBody
    @RequestMapping("login")
    public ResultInfo userLogin(String phone,String password){
        ResultInfo resultInfo=new ResultInfo();

        try {
            BasUser basUser = basUserService.userLogin(phone, password);
            session.setAttribute("user",basUser);
            System.out.println(basUser.getMobile());
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }
        catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;

    }

    /**
     * 用户快捷登陆
     * @param phone
     * @param picCode
     * @param code
     * @return
     */
    @RequestMapping("quickLogin")
    @ResponseBody
    public ResultInfo quickLogin(String phone,String picCode,String code){
        ResultInfo resultInfo=new ResultInfo();
        String sessionPicCode=(String)session.getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        if(StringUtils.isBlank(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("验证码已失效");
            return  resultInfo;
        }
        if(!sessionPicCode.equals(picCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("验证码不匹配");
            return  resultInfo;
        }
        //获取发送验证码的时间
        Date sessionTime=(Date) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
        if(null==sessionTime){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码已失效");
            return resultInfo;
        }
        Date currTime=new Date();
        long time=(currTime.getTime()-sessionTime.getTime())/1000;

        if(time>180){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码已失效");
            return resultInfo;
        }
        String sessionCode=(String) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
        if(!sessionCode.equals(code)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码不正确");
            return resultInfo;
        }

        try {
            BasUser basUser = basUserService.quickLogin(phone);
            session.setAttribute("user",basUser);
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }

        return  resultInfo;
    }

    /**
     * 退出操作
     * @param request
     * @return
     */
    @RequestMapping("exit")
    public String exit(HttpServletRequest request){
        session.removeAttribute("user");
        return "login";
    }
    /**
     * 用户认证判断
     */
    @RequestMapping("userAuthCheck")
    @ResponseBody
    public ResultInfo userAuthCheck(){
        BasUser basUser=(BasUser)session.getAttribute("user");
        return basUserSecurityService.userAuthCheck(basUser.getId());

    }
    /**
     * 转发到用户认证界面
     */
    @RequestMapping("auth")
    public String toAuthPage(){
        return "user/auth";
    }

    @RequestMapping("userAuth")
    @ResponseBody
    public ResultInfo updateUserAuth(String realName,String idCard,String businessPassword,String confirmPassword){
        BasUser basUser=(BasUser)session.getAttribute("user");
        ResultInfo resultInfo=new ResultInfo();

        try {
            basUserSecurityService.doUserAuth(realName,idCard,businessPassword,confirmPassword,basUser.getId());
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setMsg(e.getErrorMsg());
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
        }catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }

        return resultInfo;


    }
}
