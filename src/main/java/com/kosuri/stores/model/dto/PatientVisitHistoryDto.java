package com.kosuri.stores.model.dto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PatientVisitHistoryDto {
    private String visitOrdNo;
    private String cid;
    private LocalDate visitingDate;
    private String causeOfVisit;
    private String medication;
    private String treatedBy;
    private String referredTo;
}
