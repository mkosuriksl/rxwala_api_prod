package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.MembershipDetailsEntity;
import com.kosuri.stores.dao.MembershipDetailsRepository;
import com.kosuri.stores.dao.MembershipHdrEntity;
import com.kosuri.stores.dao.RazorPayCred;
import com.kosuri.stores.dao.RazorPaymentRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.exception.ServiceException;
import com.kosuri.stores.model.request.RenewMembershipVerificationRequest;
import com.kosuri.stores.model.request.RenewStoreMembershipRequest;
import com.kosuri.stores.model.response.CreateRenewMemberStoreResponse;
import com.kosuri.stores.model.response.RenewalStoreMemberships;
import com.kosuri.stores.utils.RandomUtils;

@Service
public class MembershipHdrService {

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private StoreRepository storeRepository;
	
	@Autowired
	private MembershipDetailsRepository membershipDetailsRepository;
	
	@Autowired
	private RazorPaymentRepository razorPaymentRepository;

//	public String addRenewStoreMembershipReq(List<RenewStoreMembershipRequest> requestList) {
//		String res = "";
//		try {
//			requestList.forEach(req -> {
//				validateRenewStoreMembershipReq(req.getStoreId());
//			});
//			int sum = requestList.stream().mapToInt(RenewStoreMembershipRequest::getAmount).sum();
//			MembershipHdrEntity membershipHdrEntity = repositoryHandler
//					.addMembershipHdr(createMembershipHdrFromRequest(requestList.get(0), sum));
//			requestList.forEach(request -> {
//				repositoryHandler
//						.addMembershipDetails(createMembershipDetailsEntityFromRequest(request, membershipHdrEntity));
//			});
//			res = membershipHdrEntity.getOrderId();
//		} catch (ServiceException e) {
//			throw new ServiceException(e.getMessage());
//		}
//		return res;
//	}

	 public CreateRenewMemberStoreResponse addRenewStoreMembershipReq(List<RenewStoreMembershipRequest> requestList) {
	        CreateRenewMemberStoreResponse renewStoreMembershipRes = new CreateRenewMemberStoreResponse();

	        try {
	            // Validate Store ID for each request
	            requestList.forEach(req -> validateRenewStoreMembershipReq(req.getStoreId()));

	            // Calculate total amount
	            int totalAmount = requestList.stream().mapToInt(RenewStoreMembershipRequest::getAmount).sum();

	            // Save membership header
	            MembershipHdrEntity membershipHdrEntity = repositoryHandler
	                    .addMembershipHdr(createMembershipHdrFromRequest(requestList.get(0), totalAmount));

	            // Save membership details
	            requestList.forEach(request -> {
	                repositoryHandler
	                        .addMembershipDetails(createMembershipDetailsEntityFromRequest(request, membershipHdrEntity));
	            });

	            // Fetch RazorPay credentials
	            Optional<RazorPayCred> payCredOpt = razorPaymentRepository.findById(1L);

	            // Set the key value
	            if (payCredOpt.isPresent()) {
	                renewStoreMembershipRes.setKey(payCredOpt.get().getKey());
	            } else {
	                renewStoreMembershipRes.setKey("N/A"); // Default if no key is found
	            }

	            // Set response values
	            renewStoreMembershipRes.setOrderId(membershipHdrEntity.getOrderId());
	            renewStoreMembershipRes.setOrderAmount(membershipHdrEntity.getOrderAmount());
	            renewStoreMembershipRes.setResponseMessage("Store Membership Renewed Request Added successfully!");
	            renewStoreMembershipRes.setDetails(null); // No details provided in the request

	        } catch (ServiceException e) {
	            throw new ServiceException(e.getMessage());
	        }

	        return renewStoreMembershipRes;
	    }
	public String verifyRenewStoreMembershipReq(RenewMembershipVerificationRequest request) {
		String orderId = String.valueOf(request.getOrderId());
		Optional<MembershipHdrEntity> byOrderId = repositoryHandler.findByOrderId(orderId);
		if (byOrderId.isEmpty()) {
			throw new ServiceException("Membership hdr by orderId Not Found.");
		}
		MembershipHdrEntity membershipHdrEntity = byOrderId.get();
		membershipHdrEntity.setIsVerified(Boolean.TRUE);
		Optional<MembershipDetailsEntity> membershipDetailsByOrderId = repositoryHandler
				.findMembershipDetailsByOrderId(membershipHdrEntity);
		if (membershipDetailsByOrderId.isEmpty()) {
			throw new ServiceException("Membership hdr details by orderId Not Found.");
		}
		Optional<StoreEntity> byId = storeRepository.findById(membershipDetailsByOrderId.get().getStoreId());
		if (byId.isEmpty()) {
			throw new ServiceException("Store Info Not Found.");
		}
		StoreEntity storeEntity = byId.get();
		if (Objects.isNull(storeEntity.getExpiryDate()) || !RandomUtils.isValidDate(storeEntity.getExpiryDate())) {
			throw new ServiceException(
					"Invalid or missing store info expiry date. Please provide the date in the format 'yyyy-MM-dd'.");
		}
		LocalDate existingExpiryDate = LocalDate.parse(storeEntity.getExpiryDate(), DateTimeFormatter.ISO_LOCAL_DATE);
		String newExpiryDate = existingExpiryDate
				.plusDays(Long.parseLong(membershipDetailsByOrderId.get().getNoOfDays()))
				.format(DateTimeFormatter.ISO_LOCAL_DATE);
		storeEntity.setExpiryDate(newExpiryDate);
		storeEntity.setStatus("ACTIVE");
		membershipDetailsByOrderId.get().setExpiryDate(LocalDateTime.of(existingExpiryDate, LocalTime.MIDNIGHT));
		repositoryHandler.addMembershipHdr(membershipHdrEntity);
		repositoryHandler.addMembershipDetails(membershipDetailsByOrderId.get());
		return orderId;
	}

	public List<RenewalStoreMemberships> getAllRenewalStoreMembership(Boolean status, String planId, String storeId,
			String orderId, String noOfDays, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(0, 15);
		if (Objects.nonNull(pageNo) && Objects.nonNull(pageSize)) {
			pageable = PageRequest.of(pageNo, pageSize);
		}
		return repositoryHandler.findAllFromMembershipDetails(pageable, planId, storeId, orderId, noOfDays, status);
	}

	boolean validateRenewStoreMembershipReq(String storeId) {
		Optional<StoreEntity> store = storeRepository.findById(storeId);
		if (store.isEmpty()) {
			throw new ServiceException("Store Info Not Found.");
		}
		return true;
	}

	private MembershipHdrEntity createMembershipHdrFromRequest(RenewStoreMembershipRequest request, Integer sum) {
		MembershipHdrEntity membershipEntity = new MembershipHdrEntity();
		membershipEntity.setOrderAmount(sum);
		membershipEntity.setUserId(Objects.nonNull(request.getUserId()) ? request.getUserId() : "");
		membershipEntity
				.setPaymentMethod(Objects.nonNull(request.getPaymentMethod()) ? request.getPaymentMethod() : "");
		membershipEntity.setIsVerified(Boolean.FALSE);
		membershipEntity.setOrderDate(getCurrentISTDateTime());
		return membershipEntity;
	}

	private MembershipDetailsEntity createMembershipDetailsEntityFromRequest(RenewStoreMembershipRequest request,
			MembershipHdrEntity membershipHdrEntity) {
		MembershipDetailsEntity membershipDetailsEntity = new MembershipDetailsEntity();
		membershipDetailsEntity.setPlanId(request.getPlanId());
		membershipDetailsEntity.setStoreId(request.getStoreId());
		membershipDetailsEntity.setNoOfDays(request.getNoOfDays());
		membershipDetailsEntity.setAmount(request.getAmount());
		membershipDetailsEntity.setOrderId(membershipHdrEntity);
		
		long existingCount = membershipDetailsRepository.countMembershipDetailsByOrderId(membershipHdrEntity.getOrderId());

	    String lineItemId = membershipHdrEntity.getOrderId() + "_" + String.format("%03d", existingCount + 1);
	    membershipDetailsEntity.setLineitemId(lineItemId);
	    
	    String userIdStoreId = membershipHdrEntity.getUserId() + "_" + request.getStoreId();
	    membershipDetailsEntity.setUserIdStoreId(userIdStoreId);
		return membershipDetailsEntity;
	}

	public LocalDateTime getCurrentISTDateTime() {
		return LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
	}

}
