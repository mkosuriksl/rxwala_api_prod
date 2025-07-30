package com.kosuri.stores.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerLogInDto {
    private long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean mobileVerified;
    private String password;
    private Date updatedDate;

}
