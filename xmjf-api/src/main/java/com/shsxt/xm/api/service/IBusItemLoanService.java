package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.BusItemLoan;

public interface IBusItemLoanService {
    public BusItemLoan queryBusItemLoanByItemId(Integer itemId);
}
