package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.CustomerMembershipRequest;
import com.kosuri.stores.dao.CustomerMembershipRequestRepo;
import com.kosuri.stores.dao.CustomerPaymentDetailRepo;
import com.kosuri.stores.dao.CustomerRetailerOrderHdrEntity;
import com.kosuri.stores.dao.CustomerRetailerOrderHdrRepo;
import com.kosuri.stores.dao.CustomerrPaymentDetail;
import com.kosuri.stores.dao.RazorPayCred;
import com.kosuri.stores.dao.RazorPaymentRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.OrderRequest;
import com.kosuri.stores.model.dto.TranscationDetail;
import com.kosuri.stores.model.dto.UserPaymentRequestDto;
import com.kosuri.stores.model.enums.OrderStatus;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class CustomerPaymentService {

	private final RazorpayClient razorpayClient;
	
	private final RazorPaymentRepository razorPaymentRepository;

	RazorPayCred payCred;
	
	@Autowired
	private CustomerRetailerOrderHdrRepo customerRetailerOrderHdrRepo;
	
	@Autowired
	private CustomerMembershipRequestRepo customerMembershipRequestRepo;
	
	@Autowired
	private CustomerPaymentDetailRepo customerPaymentDetailRepo;
	
	public CustomerPaymentService(RazorPaymentRepository razorPaymentRepository) throws Exception {
		this.razorPaymentRepository = razorPaymentRepository;
		payCred = razorPaymentRepository.findById(1L)
				.orElseThrow(() -> new ResourceNotFoundException(" Payment Details not Found "));
		this.razorpayClient = new RazorpayClient(payCred.getKey(), payCred.getKeySercret());
	}

	
	
	public Map<String, Object> createCustomerOrder(OrderRequest orderRequest) {
		try {
			// Build the ordeer request
			JSONObject orderRequestJson = new JSONObject();
			orderRequestJson.put("amount", orderRequest.getAmount() * 100);
			orderRequestJson.put("currency", payCred.getCurrency());
			orderRequestJson.put("receipt", orderRequest.getReceipt());

			JSONObject notes = new JSONObject(orderRequest.getNotes());
			orderRequestJson.put("notes", notes);

			// Create the order using Razorpay API
			Order order = razorpayClient.orders.create(orderRequestJson);
			
			
			CustomerRetailerOrderHdrEntity mhe = customerRetailerOrderHdrRepo
					.findByOrderId(orderRequest.getOrderId())
					.orElseThrow(() -> new ResourceNotFoundException("CustomerRetailerOrderHdrEntity Request OrderId Details not Found "));
			
			CustomerMembershipRequest msr = new CustomerMembershipRequest();

			msr.setUpdatedDate(LocalDateTime.now());
			msr.setPurchaseDate(LocalDateTime.now());
			msr.setOrderId(mhe.getOrderId());
			msr.setOrderAmount(orderRequest.getAmount());
			msr.setCustomerOrderId(order.get("id"));
			msr.setCustomerOrderStatus(OrderStatus.COMPLETED);
			// od.setBillingAddress(request.getBillingAddress());
			msr = customerMembershipRequestRepo.save(msr);
			return Map.of("oderDetails", prepareTransaction(order), "key", payCred.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			// return Map.of("Error while creating order: ", e.getMessage(), "isSuccess",
			// "false");
			return Map.of("Error while creating order: ", e.getMessage());
		}
	}
	
	public TranscationDetail prepareTransaction(Order order) {
		String orderId = order.get("id");
		String currency = order.get("currency");
		Integer amount = order.get("amount");
		TranscationDetail transaction = new TranscationDetail(orderId, currency, amount / 100);
		return transaction;
	}
	
	@Transactional
	public void saveCustomerPaymentDetails(UserPaymentRequestDto savePaymentRequest) {
		Optional<CustomerMembershipRequest> payment = customerMembershipRequestRepo
				.findByCustomerOrderId(savePaymentRequest.getRazorPayOrderId());
		if (payment.isPresent()) {
			payment.get().setPurchaseDate(LocalDateTime.now());
			payment.get().setUpdatedDate(LocalDateTime.now());
			payment.get().setTransactionNo(savePaymentRequest.getRazorPaymentId());
		}
		try {
			Payment savePayment = razorpayClient.payments.fetch(savePaymentRequest.getRazorPaymentId());
			CustomerrPaymentDetail paymentDetail = new CustomerrPaymentDetail();
			mapToPaymentEntity(paymentDetail, savePayment);
			paymentDetail.setRazorPaySignature(savePaymentRequest.getRazorPaySignature());
			paymentDetail.setMembershipRequest(payment.get());
			customerPaymentDetailRepo.save(paymentDetail);

		} catch (RazorpayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private CustomerrPaymentDetail mapToPaymentEntity(CustomerrPaymentDetail paymentDetail, Payment savePayment) {

		paymentDetail.setId(savePayment.get("id"));
		paymentDetail.setEntity(savePayment.get("entity"));
		Integer amount = savePayment.get("amount");
		Integer paymentAmount = amount != null ? amount / 100 : 0;
		paymentDetail.setAmount(paymentAmount);
		paymentDetail.setCurrency(savePayment.get("currency"));
		paymentDetail.setStatus(savePayment.get("status"));
//		paymentDetail.setRazorPaySignature(savePayment.get("signature"));
		paymentDetail.setOrder_id(savePayment.get("order_id"));
		paymentDetail.setInvoice_id(null);
		paymentDetail.setInternational(false);
		paymentDetail.setMethod(savePayment.get("method"));
		paymentDetail.setAmount_refunded(((Number) savePayment.get("amount_refunded")).longValue());
		paymentDetail.setRefund_status(JSONObject.NULL.equals(savePayment.get("refund_status")) ? null
				: String.valueOf(savePayment.get("refund_status")));
		paymentDetail.setCaptured(true);
		paymentDetail.setDescription(savePayment.get("Test Transaction"));
		paymentDetail.setCard_id(
				JSONObject.NULL.equals(savePayment.get("card_id")) ? null : String.valueOf(savePayment.get("card_id")));
		paymentDetail.setBank(savePayment.get("bank"));
		paymentDetail.setWallet(null);
		Object vpaObj = savePayment.get("vpa");
		paymentDetail.setVpa(vpaObj != null && !JSONObject.NULL.equals(vpaObj) ? vpaObj.toString() : null);
		paymentDetail.setEmail(savePayment.get("email"));
		paymentDetail.setContact(savePayment.get("contact"));
		paymentDetail.setFee(savePayment.get("fee"));
		Object taxObject = savePayment.get("tax");

		// Check if tax is an Integer
		if (taxObject instanceof Integer) {
			paymentDetail.setTax(((Integer) taxObject).longValue());
		} else if (taxObject instanceof String) {
			// If it's a String, parse it to Long
			paymentDetail.setTax(Long.parseLong((String) taxObject));
		} else {
			// Handle cases where it's neither Integer nor String, if needed
			paymentDetail.setTax(0L); // Default value
		}
		paymentDetail.setError_code(null);
		paymentDetail.setError_description(null);
		paymentDetail.setError_source(null);
		paymentDetail.setError_step(null);
		paymentDetail.setError_reason(null);
		Object createdAtObj = savePayment.get("created_at");

		if (createdAtObj instanceof Date) {
			paymentDetail.setCreated_at(((Date) createdAtObj).getTime()); // Convert Date to timestamp
		} else if (createdAtObj instanceof Number) {
			paymentDetail.setCreated_at(((Number) createdAtObj).longValue());
		} else if (createdAtObj instanceof String) {
			try {
				paymentDetail.setCreated_at(Long.parseLong((String) createdAtObj));
			} catch (NumberFormatException e) {
				paymentDetail.setCreated_at(0L); // Default value if parsing fails
			}
		} else {
			paymentDetail.setCreated_at(0L); // Default value for unknown types
		}

		return paymentDetail;

	}


}
