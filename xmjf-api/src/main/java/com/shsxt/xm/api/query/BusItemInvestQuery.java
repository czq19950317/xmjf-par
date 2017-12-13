package com.shsxt.xm.api.query;

import java.io.Serializable;

public class BusItemInvestQuery extends BaseQuery implements Serializable {
    private static final long serialVersionUID = -2082183891611134197L;
    private Integer itemId;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
