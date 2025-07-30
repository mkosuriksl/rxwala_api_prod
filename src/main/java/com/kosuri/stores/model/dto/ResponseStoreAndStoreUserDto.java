package com.kosuri.stores.model.dto;

import java.util.List;

import com.kosuri.stores.dao.StoreAndStoreUserEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.TabStoreUserEntity;

import lombok.Data;

@Data
public class ResponseStoreAndStoreUserDto {
	private String message;
	private boolean status;
	private List<StoreAndStoreUserEntity> storeAndStoreUser;
	private List<TabStoreUserEntity> storeTabUser;
	private List<StoreEntity> storeInfo;
}
