package com.kosuri.stores.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kosuri.stores.dao.StoreEntity;

import lombok.Data;

@Data
public class StoreDto {
	private String type;
    private String userIdStoreId;
    private String id;
    private String name;
    private String pincode;
    private String district;
    private String state;
    private String location;
    private String owner;
    private String ownerContact;
    private String secondaryContact;
    private String ownerEmail;
    @JsonIgnore
    private LocalDate registrationDate;
    @JsonIgnore
    private String creationTimeStamp;
    @JsonIgnore
    private String role;
    @JsonIgnore
    private String addedBy;
    @JsonIgnore
    private String modifiedBy;
    @JsonIgnore
    private String modifiedDate;
    @JsonIgnore
    private String modifiedTimeStamp;
    @JsonIgnore
    private String status;
    @JsonIgnore
    private String storeVerifiedStatus;
    @JsonIgnore
    private String expiryDate;
    @JsonIgnore
    private String currentPlan;
    @JsonIgnore
    private String storeBusinessType;
    @JsonIgnore
    private String userId;

    public StoreDto(StoreEntity store) {
        this.type = store.getType();
        this.userIdStoreId = store.getUserIdStoreId();
        this.id = store.getId();
        this.name = store.getName();
        this.pincode = store.getPincode();
        this.district = store.getDistrict();
        this.state = store.getState();
        this.location = store.getLocation();
        this.owner = store.getOwner();
        this.ownerContact = store.getOwnerContact();
        this.secondaryContact = store.getSecondaryContact();
        this.ownerEmail = store.getOwnerEmail();
        this.registrationDate = store.getRegistrationDate();
        this.creationTimeStamp = store.getCreationTimeStamp();
        this.role = store.getRole();
        this.addedBy = store.getAddedBy();
        this.modifiedBy = store.getModifiedBy();
        this.modifiedDate = store.getModifiedDate();
        this.modifiedTimeStamp = store.getModifiedTimeStamp();
        this.status = store.getStatus();
        this.storeVerifiedStatus = store.getStoreVerifiedStatus();
        this.expiryDate = store.getExpiryDate();
        this.currentPlan = store.getCurrentPlan();
        this.storeBusinessType = store.getStoreBusinessType();
        this.userId = store.getUserId();
    }

}
