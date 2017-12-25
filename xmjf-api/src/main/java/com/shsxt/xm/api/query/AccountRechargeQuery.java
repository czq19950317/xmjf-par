package com.shsxt.xm.api.query;

import java.io.Serializable;

public class AccountRechargeQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = 5341261202629176762L;
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
