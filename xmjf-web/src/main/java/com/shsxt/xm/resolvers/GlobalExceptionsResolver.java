package com.shsxt.xm.resolvers;

import com.alibaba.fastjson.JSON;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.AuthExcetion;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class GlobalExceptionsResolver implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView=getDefaultModelAndView(request);
        //用户未登录，转到登陆界面
        if(handler instanceof HandlerMethod){
            if(ex instanceof AuthExcetion){
                try {
                    response.sendRedirect(request.getContextPath()+"/login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return  null;
            }
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            Method method=handlerMethod.getMethod();
            ResponseBody responseBody=method.getAnnotation(ResponseBody.class);
            if(null!=responseBody){
                /**
                 * 方法响应内容为json
                 */
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
                resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
                if(ex instanceof ParamsExcetion){
                    ParamsExcetion pe=(ParamsExcetion)ex;
                    resultInfo.setMsg(pe.getErrorMsg());
                    resultInfo.setCode(pe.getErrorCode());

                }
                /**
                 * 响应json到浏览器
                 */
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json;charset=utf-8");
                PrintWriter pw = null;
                try {
                    pw=response.getWriter();
                    pw.write(JSON.toJSONString(resultInfo));
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    pw.flush();
                    if(null!=pw){
                        pw.close();
                    }
                }
                return null;
            }else {
                /**
                 * 方法响应信息为视图
                 */
                if(ex instanceof ParamsExcetion){
                    ParamsExcetion pe=(ParamsExcetion)ex;
                    modelAndView.addObject("msg",pe.getErrorMsg());
                    modelAndView.addObject("code",pe.getErrorCode());
                }
                return modelAndView;
            }
        }else{
            return modelAndView;
        }




        
    }

    private ModelAndView getDefaultModelAndView(HttpServletRequest request) {
        ModelAndView modelAndView=new ModelAndView("error");
        modelAndView.addObject("ctx",request.getContextPath());
        modelAndView.addObject("msg", P2PConstant.OPS_FAILED_MSG);
        modelAndView.addObject("code",P2PConstant.OPS_FAILED_CODE);
        return modelAndView;

    }
}
