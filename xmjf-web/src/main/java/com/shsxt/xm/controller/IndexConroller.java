package com.shsxt.xm.controller;

import com.shsxt.xm.context.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexConroller extends BaseController {
   /*@Autowired(required = false)
   private HttpServletRequest request;*/

    /**
     * 主页
     * @return
     */
    @RequestMapping("index")
    public String index(){
      // request.setAttribute("ctx",request.getContextPath());
        return "index";
    }

    /**
     * 登陆页面
     * @return
     */
    @RequestMapping("login")
    public String login(){
       //request.setAttribute("ctx",request.getContextPath());
        return "login";
    }

    /**
     * 注册页面
     * @return
     */
    @RequestMapping("register")
    public String register(){
       //request.setAttribute("ctx",request.getContextPath());
        return "register";
    }
    /**
     * 快速登陆
     */
    @RequestMapping("quickLogin")
    public String quickLogin(){
        return "quick_login";
    }
}
