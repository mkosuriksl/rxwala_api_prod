package com.kosuri.stores.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request_method")
public class RequestMethod {

	@Id
	@Column(name = "serviceRequest_Id")
	private String serviceRequestId;
	
	@Column(name = "requested_method")
	private String requestedMethod;

}
