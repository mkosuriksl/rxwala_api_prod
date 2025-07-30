package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table(name = "distributor")
public class DistributorEntity {


    @Id
    @Nonnull
    private @Column(name = "userid_distributerid") String id;
    private @Column(name = "User_id") String userId;
    private @Column(name = "Distributer_Id") String retailerId;
    private @Column(name = "Distributer_Name") String retailerName;
    private @Column(name = "credit_limit") Integer creditLimit;
    private @Column(name = "Updated_by") String updatedBy;
    private @Column(name = "Updated_date") String updatedDate;
}
