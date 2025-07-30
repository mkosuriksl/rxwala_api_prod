package com.kosuri.stores.model.response;

import com.kosuri.stores.model.purchaseReport.PurchaseReport;
import com.kosuri.stores.model.purchaseReport.PurchaseReportRecord;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GeneratePurchaseReportResponse {
    private List<PurchaseReportRecord> purchaseReport;

    private String msg;

    public List<PurchaseReportRecord> getPurchaseReport() {
        return purchaseReport;
    }

    public void setPurchaseReport(List<PurchaseReportRecord> purchaseReport) {
        this.purchaseReport = purchaseReport;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
