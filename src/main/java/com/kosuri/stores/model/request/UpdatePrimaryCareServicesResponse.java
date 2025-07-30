package com.kosuri.stores.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kosuri.stores.dao.PrimaryCareEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignores null values in JSON response
public class UpdatePrimaryCareServicesResponse {
	private String message;
    private PrimaryCareEntity updatedServices;

}
