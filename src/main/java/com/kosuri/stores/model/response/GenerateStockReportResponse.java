package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.model.purchaseReport.StockRecord;

import java.util.List;

public class GenerateStockReportResponse {
    private List<StockRecord> stockReport;
    private String msg;

    public List<StockRecord> getStockReport() {
        return stockReport;
    }

    public void setStockReport(List<StockRecord> stockReport) {
        this.stockReport = stockReport;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
