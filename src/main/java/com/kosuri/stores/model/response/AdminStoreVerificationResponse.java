package com.kosuri.stores.model.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminStoreVerificationResponse {

	private Integer verificationId;
	private String storeId;
	private String storeCategory;
	private String doc1;
	private String doc2;
	private String doc3;
	private String doc4;
	private String verifiedBy;
	private LocalDateTime verificationDate;
	private String comment;
	private String verificationStatus;
	private String userId;
}
