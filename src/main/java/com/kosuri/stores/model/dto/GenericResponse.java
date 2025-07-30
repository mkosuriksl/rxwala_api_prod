package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class GenericResponse<T> {
    private String status;
    private String message;
    private T data;

    // Constructors
    public GenericResponse() {}

    public GenericResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

