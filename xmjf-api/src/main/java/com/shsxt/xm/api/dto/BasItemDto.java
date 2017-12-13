package com.shsxt.xm.api.dto;

import com.shsxt.xm.api.po.BasItem;

import java.io.Serializable;


public class BasItemDto extends BasItem implements Serializable {
    private static final long serialVersionUID = -1338472780985117857L;
    private Integer total;
    private long syTime;

    public long getSyTime() {
        return syTime;
    }

    public void setSyTime(long syTime) {
        this.syTime = syTime;
    }

    @Override
    public String toString() {
        return super.toString()+"BasItemDto{" +
                "total=" + total +
                '}';
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
