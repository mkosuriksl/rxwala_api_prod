package com.kosuri.stores.handler;


import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRetailerOrderHdrEntity;
import com.kosuri.stores.dao.CustomerRetailerOrderHdrRepo;
import com.kosuri.stores.dao.OrderUpdatedEntity;
import com.kosuri.stores.dao.OrderUpdatedRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.OrderUpdatedDto;
import com.kosuri.stores.model.enums.OrderStatus;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class OrderUpdatedHandler {

    @Autowired
    private OrderUpdatedRepository orderUpdatedRepo;

    @Autowired
    private CustomerRetailerOrderHdrRepo orderHdrRepo; // Assuming you have a repository for OrderHdrEntity

    @Autowired
    private TabStoreRepository tabStoreRepository;
    public OrderUpdatedDto saveOrderUpdate(String orderId, OrderUpdatedDto update) {

    	String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
		}
        // Retrieve the order header entity
        CustomerRetailerOrderHdrEntity orderHdr = orderHdrRepo.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Create and save the order update entity
        OrderUpdatedEntity orderUpdate = new OrderUpdatedEntity();

        orderUpdate.setOrderId(orderHdr.getOrderId());
        OrderStatus inputStatus = OrderStatus.valueOf(update.getOrderStatus());
        orderUpdate.setOrderStatus(inputStatus);
//        orderUpdate.setOrderStatus(OrderStatus.valueOf(update.getOrderStatus()));
        orderUpdate.setOrderUpdatedDate(update.getOrderUpdatedDate());
        orderUpdate.setDeliveryMethod(update.getDeliveryMethod());
        if (inputStatus == OrderStatus.DELIVERED) {
            orderUpdate.setPaymentStatus("SUCCESS");
        } else {
            orderUpdate.setPaymentStatus(update.getPaymentStatus()); //it's not input instead of success(dependends on OrderStatus)
        }
        orderUpdate.setUpdatedBy(loginStore.get().getUserId());
        // Set other attributes of orderUpdate as needed

        orderUpdate=orderUpdatedRepo.save(orderUpdate);

        OrderUpdatedDto savedUpdate = new OrderUpdatedDto();
        savedUpdate.setOrderId(orderUpdate.getOrderId());
        savedUpdate.setOrderStatus(orderUpdate.getOrderStatus().name());
        savedUpdate.setOrderUpdatedDate(orderUpdate.getOrderUpdatedDate());
        savedUpdate.setUpdatedBy(orderUpdate.getUpdatedBy());
        savedUpdate.setDeliveryMethod(orderUpdate.getDeliveryMethod());
        savedUpdate.setPaymentStatus(orderUpdate.getPaymentStatus());
        return savedUpdate;
    }


}
