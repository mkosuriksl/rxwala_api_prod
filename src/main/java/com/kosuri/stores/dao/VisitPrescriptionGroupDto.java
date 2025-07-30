package com.kosuri.stores.dao;

import java.util.List;

import lombok.Data;

@Data
public class VisitPrescriptionGroupDto {
    private String visitOrdNo;
    private List<PrescriptionHistory> prescriptionOldHistory;
}
