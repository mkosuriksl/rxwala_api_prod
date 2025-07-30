package com.kosuri.stores.model.response;

import java.util.List;

import com.kosuri.stores.dao.AdminStoreMembershipEntity;

import lombok.Data;

@Data
public class GetStoreMembershipResponse extends GenericResponse {
	private List<AdminStoreMembershipEntity> storeMembershipList;

}