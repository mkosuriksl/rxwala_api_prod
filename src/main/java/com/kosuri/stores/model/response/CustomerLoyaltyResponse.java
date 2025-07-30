package com.kosuri.stores.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.DecimalFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerLoyaltyResponse {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Double totalSalesVolume;

    private Integer loyaltyPoints;
    private Double discountEligible;

    private static final DecimalFormat df = new DecimalFormat("0.00");


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getTotalSalesVolume() {
        return totalSalesVolume;
    }

    public void setTotalSalesVolume(Double totalSalesVolume) {
        this.totalSalesVolume = Double.parseDouble(df.format(totalSalesVolume));
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Double getDiscountEligible() {
        return discountEligible;
    }

    public void setDiscountEligible(Double discountEligible) {
        if(discountEligible!=null){
             this.discountEligible = Double.parseDouble(df.format(discountEligible));
        }
    }
}
