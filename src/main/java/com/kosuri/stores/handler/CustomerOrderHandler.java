package com.kosuri.stores.handler;

import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.*;
import com.kosuri.stores.model.enums.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerOrderHandler {

    @Autowired
    private CustomerRetailerOrderHdrRepo orderHdrRepo;
    @Autowired
    private CustomerRetailerOrderDetailsRepo orderDetailsRepo;
    @Autowired
    private StockHandler stockHandler;
    @Autowired
    private CustomerRegisterRepository customerRegisterRepo;

    @Autowired
    private OrderUpdatedRepository orderUpdatedRepo;
    
    @Autowired
    private CustomerOrderDeliveryStatusRepostiory customerOrderDeliveryStatusRepostiory;

    @Autowired
    private CustomerRegisterRepository customerRegisterRepository;
    
    @Autowired
    private TabStoreRepository tabStoreRepository;
    
    @Autowired
    private StoreRepository storeRepo;
    @Transactional
    public CustomerRetailerOrderHdrEntity placeOrder(OrderRequestDto dto) {
    	
    	String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
        BigDecimal orderAmount = dto.getOrderAmount();
        float gst=dto.getGstTotal();


        // Create order header
        CustomerRetailerOrderHdrEntity orderHdr = new CustomerRetailerOrderHdrEntity();
        orderHdr.setOrderStatus(OrderStatus.PENDING);
        orderHdr.setOrderAmount(orderAmount);
        orderHdr.setGstTotal(gst);
        orderHdr.setPaymentStatus(OrderStatus.PENDING);
        orderHdr.setDeliveryMethod(dto.getDeliveryMethod());//input
        orderHdr.setCustomerId(dto.getCustomerId());
        orderHdr.setRetailerId(dto.getUserIdstoreId());
        orderHdr.setOrderUpdatedBy("System");
        orderHdr.setOrderDate(LocalDate.now());
        orderHdr.setOrderUpdatedDate(new Date());
        orderHdrRepo.save(orderHdr);
        
        CustomerOrderDeliveryStatus deliveryStatus = new CustomerOrderDeliveryStatus();
        deliveryStatus.setOrderId(orderHdr.getOrderId()); // assuming getOrderId() returns the saved order's ID
        deliveryStatus.setCustomerId(dto.getCustomerId());
//        deliveryStatus.setDeliveryDate(null); // You can set this later when delivery happens
        deliveryStatus.setDeliveryLocation(dto.getLocation());
        String updatedBy;
		if (login.isPresent()) {
			updatedBy = login.get().getCId(); // Customer ID
		} else {
			updatedBy = loginStore.get().getUserId(); // Store User ID
		}
        deliveryStatus.setUpdatedBy(updatedBy);
        deliveryStatus.setPaymentStatus(OrderStatus.PENDING);
        deliveryStatus.setDelivaryStatus(OrderStatus.PENDING);
        customerOrderDeliveryStatusRepostiory.save(deliveryStatus); 

        // Create and save order details
        List<OrderDetailsDto> orderDetailsList = dto.getOrderDetailsList();

        	int itemCounter = 1;

            for (OrderDetailsDto orderDetails : orderDetailsList) {
                if (!stockHandler.checkStockAvailabilityTwo(dto, dto.getLocation(), orderDetails.getItemName(),
                        orderDetails.getManufactureName())) {
                    throw new RuntimeException("Stock not available for product: " + orderDetails.getItemName());
                }


                CustomerRetailerOrderDetailsEntity orderDetailsEntity = new CustomerRetailerOrderDetailsEntity();
                String formattedCounter = String.format("%04d", itemCounter); // 0001, 0002...
                String lineItemId = orderHdr.getOrderId() + "_" + formattedCounter;
                orderDetailsEntity.setLineItemId(lineItemId);
                itemCounter++;
                orderDetailsEntity.setItemCategory(orderDetails.getItemCategory());
                orderDetailsEntity.setItemName(orderDetails.getItemName());
                orderDetailsEntity.setOrderQty(orderDetails.getOrderQty()); // Assuming this is needed
                orderDetailsEntity.setDeliveryQty(Integer.parseInt(orderDetails.getDeliveryQty())); // Assuming this is needed
                orderDetailsEntity.setMrp(orderDetails.getMrp());
                orderDetailsEntity.setDiscount(orderDetails.getDiscount());
                orderDetailsEntity.setGst(orderDetails.getGst());
                orderDetailsEntity.setOtherOffer(orderDetails.getOtherOffer());
                orderDetailsEntity.setInvoiceId(orderDetails.getInvoiceId());
                orderDetailsEntity.setTotalAmount(orderDetails.getTotalAmount());
                orderDetailsEntity.setBrandName(orderDetails.getBrandName());
                orderDetailsEntity.setManufactureName(orderDetails.getManufactureName());
                orderDetailsEntity.setBatchName(orderDetails.getBatchName());
                orderDetailsEntity.setExpiryDate(orderDetails.getExpiryDate());
                orderDetailsEntity.setItemCode(orderDetails.getItemCode());
                orderDetailsEntity.setCustomerRetailerOrderHdr(orderHdr);
                orderDetailsRepo.save(orderDetailsEntity);

            }

        return orderHdr;
    }



    @Transactional
    public List<OrderDetailsCustomerDto> getOrderDetailsAndCustomerByParams(String orderId, String location, String customerNumber, String customerEmail,
    		LocalDate fromDate, LocalDate toDate) throws InstantiationException, IllegalAccessException {
        List<CustomerRetailerOrderHdrEntity> orderHdrList;

        if(customerNumber!=null ||customerEmail!=null){
        	List<CustomerRegisterEntity> customerRegisters = customerRegisterRepo.findByPhoneNumberOrEmail(customerNumber, customerEmail);

        	List<String> customerIds = customerRegisters.stream()
        	        .map(CustomerRegisterEntity::getCId)
        	        .collect(Collectors.toList());

        	orderHdrList = orderHdrRepo.findByCustomerIdIn(customerIds);

        }else if (location!=null){
            List<CustomerRegisterEntity> registerEntityList = customerRegisterRepo.findByLocation(location);
            List<String> customerIdList = new ArrayList<>();
            for (CustomerRegisterEntity registerEntity : registerEntityList) {
                customerIdList.add(registerEntity.getCId());
            }
            orderHdrList = orderHdrRepo.findByCustomerIdIn(customerIdList);
        } else if (orderId != null) {
            CustomerRetailerOrderHdrEntity orderHdr = orderHdrRepo.findByOrderId(orderId).
                    orElseThrow(() -> new IllegalArgumentException("Order not found"));
            orderHdrList = new ArrayList<>();
            orderHdrList.add(orderHdr);
        }
        else if (fromDate != null && toDate != null) {
            orderHdrList = orderHdrRepo.findByOrderDateBetween(fromDate, toDate);
        }else {
            throw new IllegalArgumentException("At least one parameter must be provided");
        }

        return buildResultDtoList(orderHdrList);
        }
    private List<OrderDetailsCustomerDto> buildResultDtoList(List<CustomerRetailerOrderHdrEntity> orderHdrList) {
        List<OrderDetailsCustomerDto> resultDtoList = new ArrayList<>();
        for (CustomerRetailerOrderHdrEntity orderHdr : orderHdrList) {
            OrderDetailsCustomerDto resultDto = new OrderDetailsCustomerDto();
            resultDto.setOrderHdr(orderHdr);
            // Fetch Customer info
            CustomerRegisterEntity customer = customerRegisterRepo.findByCId(orderHdr.getCustomerId());
            resultDto.setCustomer(customer);

            // Fetch Store info by userIdStoreId
            StoreEntity store = storeRepo.findByUserIdStoreIdone(orderHdr.getRetailerId());
            resultDto.setStore(store);

            resultDtoList.add(resultDto);
        }
        return resultDtoList;
    }


    @Transactional
    public String updateOrderQty(OrderQtyUpdateDto dto) {
        String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();

        Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
        if (loginStore.isEmpty()) {
            throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
        }

        CustomerRetailerOrderDetailsEntity details = orderDetailsRepo.findById(dto.getLineItemId())
                .orElseThrow(() -> new IllegalArgumentException("Line item not found"));

        // Update only if value is different
        if (details.getOrderQty() != dto.getOrderQty()) {
            details.setOrderQty(dto.getOrderQty());
        }

        // Update header
        CustomerRetailerOrderHdrEntity header = details.getCustomerRetailerOrderHdr();
        if (header != null) {
            header.setOrderUpdatedBy(loginStore.get().getUserId());
            header.setOrderUpdatedDate(new Date());
            header.setOrderStatus(dto.getOrderStatus());
            orderHdrRepo.save(header);
        }

        orderDetailsRepo.save(details);

        return "Order quantity and header updated successfully.";
    }






}
