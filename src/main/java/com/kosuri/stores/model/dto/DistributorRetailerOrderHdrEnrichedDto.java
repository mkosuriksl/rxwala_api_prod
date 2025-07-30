package com.kosuri.stores.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributorRetailerOrderHdrEnrichedDto {
	private String orderId;
    private Date orderDate;
    private String status;
    private Date orderUpdatedDate;
    private String orderUpdatedBy;
    private String updatedBy;
    private String retailerId;
    private String distrubutorId;
    private String storeId;
    private String invoiceNo;

    private TabStoreUserInfoDto retailerInfo;
    private TabStoreUserInfoDto distributorInfo;
}
