package com.shsxt.xm.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;


public class AccountDto implements Serializable{
    private static final long serialVersionUID = 543858229319222820L;
    private String name;
    private BigDecimal y;

    @Override
    public String toString() {
        return "AccountDto{" +
                "name='" + name + '\'' +
                ", y=" + y +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }
}
