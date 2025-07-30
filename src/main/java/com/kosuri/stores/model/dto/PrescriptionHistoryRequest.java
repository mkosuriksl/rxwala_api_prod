package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class PrescriptionHistoryRequest {
    private String medicineName;
    private int morningQty;
    private int afternoonQty;
    private int nightQty;
    private boolean beforeFood;
    private boolean afterFood;
}

