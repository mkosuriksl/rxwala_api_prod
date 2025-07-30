package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomerRetailerOrderHdrRepo extends JpaRepository<CustomerRetailerOrderHdrEntity, String> {

    Optional<CustomerRetailerOrderHdrEntity> findByOrderId(String orderId);

    List<CustomerRetailerOrderHdrEntity> findByCustomerId(String cId);

    List<CustomerRetailerOrderHdrEntity> findByCustomerIdIn(List<String> customerIdList);

	List<CustomerRetailerOrderHdrEntity> findByOrderDateBetween(LocalDate fromOrderDate, LocalDate toOrderDate);
}
