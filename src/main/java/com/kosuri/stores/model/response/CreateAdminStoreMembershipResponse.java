package com.kosuri.stores.model.response;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class CreateAdminStoreMembershipResponse extends GenericResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
}
