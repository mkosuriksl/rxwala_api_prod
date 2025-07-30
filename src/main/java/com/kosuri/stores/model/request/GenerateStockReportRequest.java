package com.kosuri.stores.model.request;

import org.springframework.http.HttpMethod;

import java.net.URI;

public class GenerateStockReportRequest extends GenerateReportRequest {
    public GenerateStockReportRequest(HttpMethod method, URI url) {
        super(method, url);
    }
}
