package com.kosuri.stores.s3.config;

public class S3ObjectDetail {
    private String url;
    private String name;
    private  Long size;
    private String key;

    public S3ObjectDetail(String url, String name, Long size) {
        this.url = url;
        this.name = name;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}