package com.kosuri.stores.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerLoyaltyResponseList extends GenericResponse {
    private List<CustomerLoyaltyResponse> CustomerLoyaltyResponseList;

    public List<CustomerLoyaltyResponse> getCustomerLoyaltyResponseList() {
        return CustomerLoyaltyResponseList;
    }

    public void setCustomerLoyaltyResponseList(List<CustomerLoyaltyResponse> customerLoyaltyResponseList) {
        CustomerLoyaltyResponseList = customerLoyaltyResponseList;
    }
    
}
