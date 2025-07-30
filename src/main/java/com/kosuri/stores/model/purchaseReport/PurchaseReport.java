package com.kosuri.stores.model.purchaseReport;

import java.util.List;

public class PurchaseReport {
    private List<PurchaseReportRecord> purchaseReportRecords;

    public List<PurchaseReportRecord> getPurchaseReportRecords() {
        return purchaseReportRecords;
    }

    public void setPurchaseReportRecords(List<PurchaseReportRecord> purchaseReportRecords) {
        this.purchaseReportRecords = purchaseReportRecords;
    }
}
