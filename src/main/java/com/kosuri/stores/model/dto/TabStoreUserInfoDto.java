package com.kosuri.stores.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TabStoreUserInfoDto {
	private String userId;
	private String type;
    private String username;
    private String storeUserContact;
    private String storeUserEmail;
}
