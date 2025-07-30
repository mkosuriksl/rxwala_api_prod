package com.kosuri.stores.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelLineStatusRequest {
    private String serviceRequestId;
    private List<String> serviceRequestLineId;
}
