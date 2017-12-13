package com.shsxt.xm.api.utils;

public enum SmsType {
    REGISTER(1),
    NOTIFY(2);
    private Integer type;

    public Integer getType() {
        return type;
    }

    SmsType(Integer type) {
        this.type = type;
    }
}
