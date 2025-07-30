package com.kosuri.stores.model.response;

import com.kosuri.stores.model.purchaseReport.PurchaseReportRecord;
import com.kosuri.stores.model.purchaseReport.SaleReportRecord;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GenerateSaleReportResponse {
    private List<SaleReportRecord> saleReport;
    private String msg;

    public List<SaleReportRecord> getSaleReport() {
        return saleReport;
    }

    public void setSaleReport(List<SaleReportRecord> saleReport) {
        this.saleReport = saleReport;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
