package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="customer_login_two")
public class CustomerLogInEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean mobileVerified;
    private String password;
    @UpdateTimestamp
    private Date updatedDate;


}
