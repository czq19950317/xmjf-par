package com.shsxt.xm.api.model;

import com.shsxt.xm.api.constant.P2PConstant;

import java.io.Serializable;

public class ResultInfo implements Serializable{
    private static final long serialVersionUID = 6479704509276746170L;
    private Integer code=200;
    private String msg= P2PConstant.OPS_SUCCESS_MSG;
    private Object result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
