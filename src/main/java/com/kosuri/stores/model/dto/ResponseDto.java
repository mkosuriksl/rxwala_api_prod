package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    private String status;
    private String message;
    private Object data;

    public ResponseDto(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
