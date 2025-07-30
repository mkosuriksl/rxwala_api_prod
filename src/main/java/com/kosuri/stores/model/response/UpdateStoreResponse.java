package com.kosuri.stores.model.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class UpdateStoreResponse extends GenericResponse {

    private String id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
