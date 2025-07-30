package com.kosuri.stores.s3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseService {
    public enum ErrorCode {
        Duplicate,
        NotFound,
        Unknown,
        AuthError,
        InvalidFormat,
        ValidationError
    }

    ObjectMapper mapper = new ObjectMapper();

    private static final int ERROR_HTTP_STATUS = 501;
    private static final int HTTP_STATUS = 200;
    private ErrorCode code;
    private String message;
    private String exception;

    private Response response;
    ResponseEntity responseEntity;

    public ResponseService(ErrorCode code, String message, String exception) {
        response = new Response(code, message, exception);
    }

    public ResponseService(ErrorCode code, String message) {
        response = new Response(code, message);
    }

    public ResponseService() {
    }

    public ResponseEntity getResponseEntity(Object object) {
        responseEntity = new ResponseEntity(object, HttpStatus.OK);
        return responseEntity;
    }

    public ResponseEntity getResponseEntity(ErrorCode errorCode, String message, String exception){
        Response response = new Response(errorCode, message, exception);
        responseEntity = new ResponseEntity(response, HttpStatus.FORBIDDEN);
        return responseEntity;
    }

    public ResponseEntity getResponseEntity(ErrorCode errorCode, String message){
        return  getResponseEntity(errorCode, message, null);
    }

    public ResponseEntity getResponseEntity(){
        return  responseEntity = new ResponseEntity(HttpStatus.OK);
    }

}
