package com.kosuri.stores.model.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PharmasistRequest {

	private String name;
	private String pharmaUserEmail;
	private String pharmaUserContact;
	private String education;
	private String experience;
	private MultipartFile experienceDoc;
	private MultipartFile pciCertifiedDoc;
	private MultipartFile pharmacyDoc;
	private MultipartFile personalPhoto;
	private String pciExpiryDate;
	private String availableLocation;
}
