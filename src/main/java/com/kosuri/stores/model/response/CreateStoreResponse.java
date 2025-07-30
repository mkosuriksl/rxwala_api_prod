package com.kosuri.stores.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateStoreResponse extends GenericResponse2 {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    
    private String userIdStoreId;

    private String token;
}
