package com.shsxt.xm.api.query;

import java.io.Serializable;

public class BasItemQuery extends BaseQuery implements Serializable{
    private static final long serialVersionUID = 1315701031029269133L;
    private Integer itemCycle;// 项目期限 1（0-30） 2（30-90） 3（90+）
    private Integer itemType;// 项目类型  2（车商保）,5(车信宝),3(车贷宝),1(学车宝)
    private Integer isHistory;// 是否为历史项目  1-历史项目  0-可投标项目

    public Integer getItemCycle() {
        return itemCycle;
    }

    public void setItemCycle(Integer itemCycle) {
        this.itemCycle = itemCycle;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public Integer getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(Integer isHistory) {
        this.isHistory = isHistory;
    }
}
