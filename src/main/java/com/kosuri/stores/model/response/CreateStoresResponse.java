package com.kosuri.stores.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Getter
@Setter
@ToString
public class CreateStoresResponse extends GenericResponse2 {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    
    @JsonIgnore
    private String userIdStoreId;

}
