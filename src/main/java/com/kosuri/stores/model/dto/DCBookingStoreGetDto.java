package com.kosuri.stores.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.kosuri.stores.model.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCBookingStoreGetDto {
	 private String serviceRequestId;
	    private Double totalAmount;
	    private String updatedBy;
	    private String customerId;
	    private String userIdStoreId; // Add this field
	    private String homeService;
	    private String walkinService;
	    private LocalDate appointmentDate;
	    private LocalTime appointmentTime;
	    private LocalDate bookingDate;
	    private Status status;
	    private List<DcBookingRequestServiceStoreDto> selectedServices;
	    private StoreDetailsDto storeDetailsDto;
		public DCBookingStoreGetDto(String serviceRequestId, Double totalAmount, String updatedBy,
				String customerId, String userIdStoreId, String homeService, String walkinService,
				LocalDate appointmentDate, LocalTime appointmentTime,LocalDate bookingDate,Status status, List<DcBookingRequestServiceStoreDto> selectedServices,
				StoreDetailsDto storeDetailsDto) {
			super();
			this.serviceRequestId = serviceRequestId;
			this.totalAmount = totalAmount;
			this.updatedBy = updatedBy;
			this.customerId = customerId;
			this.userIdStoreId = userIdStoreId;
			this.homeService = homeService;
			this.walkinService = walkinService;
			this.appointmentDate = appointmentDate;
			this.appointmentTime = appointmentTime;
			this.bookingDate=bookingDate;
			this.status=status;
			this.selectedServices = selectedServices;
			this.storeDetailsDto = storeDetailsDto;
		}
		
	    
		
	    
}