package com.kosuri.stores.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RenewMembershipVerificationRequest {

    @NotNull(message = "orderId cannot be blank")
    private Integer orderId;

}
