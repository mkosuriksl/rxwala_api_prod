package com.kosuri.stores.model.request;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

@Getter
@Setter
@ToString
public class StoreLicenceInfoRequest extends RequestEntity<StoreLicenceInfoRequest> {
    public StoreLicenceInfoRequest(HttpMethod method, URI url) {
        super(method, url);
    }


    private String storeId;
    private String pharmacyLicense;
    private String gstLicense;
    private String licenceNumber;
    private String gstNumber;
    private String pharmacyLicenseExpiry;
    private String updatedBy;
    private String updatedDate;
    private String licenseRegisteredState;
    private String licenseRegisteredDistrict;
    private String licenseRegisteredDivision;
}
