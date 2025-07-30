package com.kosuri.stores.s3.config;

import java.util.HashMap;
import java.util.List;

public class S3ObjectMaster {
    private KeyValue appBucket;
    private List<KeyValue> genericBuckets;
    private HashMap<String, List<S3ObjectDetail>> s3ObjectDetails = new HashMap<>();

    public enum GenericBuckets{
        Beverages("Beverages"),
        Dairy("Dairy"),
        FoodGrains("Food Grains"),
        Fruits("Fruits"),
        Grocery("Grocery");

        private String description;
        public String getDescription(){
            return description;
        }

        private GenericBuckets(String description){
            this.description = description;

        }
    }

    public S3ObjectMaster() {
    }

    public KeyValue getAppBucket() {
        return appBucket;
    }

    public void setAppBucket(KeyValue appBucket) {
        this.appBucket = appBucket;
    }

    public List<KeyValue> getGenericBuckets() {
        return genericBuckets;
    }

    public void setGenericBuckets(List<KeyValue> genericBuckets) {
        this.genericBuckets = genericBuckets;
    }

    public HashMap<String, List<S3ObjectDetail>> getS3ObjectDetails() {
        return s3ObjectDetails;
    }

    public void setS3ObjectDetails(HashMap<String, List<S3ObjectDetail>> s3ObjectDetails) {
        this.s3ObjectDetails = s3ObjectDetails;
    }
}
