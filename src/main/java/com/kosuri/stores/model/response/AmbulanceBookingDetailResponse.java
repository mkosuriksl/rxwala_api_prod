package com.kosuri.stores.model.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbulanceBookingDetailResponse {

	    private String bookingNo;

	    private LocalDateTime bookingDate;

	    private String patientName;

	    private String fromLocation;

	    private String toLocation;

	    private String customerContNum;

	    private String contactPerson;

	    private String bookedBy;

	    private String status;

	    private String remarks;

	    private LocalDateTime createdOn;

	    private String createdBy;

	    private LocalDateTime updatedOn;

	    private String updatedBy;

	    private String fromLocationDetail;

	    private String ambulanceRegNo;

}
