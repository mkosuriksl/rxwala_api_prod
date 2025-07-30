package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AdminStoreMembershipEntity;
import com.kosuri.stores.exception.ServiceException;
import com.kosuri.stores.model.request.CreateAdminStoreMembershipRequest;
import com.kosuri.stores.model.request.UpdateAdminStoreMembershipRequest;

@Service
public class AdminStoreMembershipHandler {

	@Autowired
	private AdminStoreMembershipRepoHandler adminStoreMembershipRepoHandler;

	@Autowired
	private StoreHandler storeHandler;

	public String createAdminStoreMembership(CreateAdminStoreMembershipRequest createAdminStoreMembershipRequest)
			throws Exception {
		try {
			if (validateAddStoreMembershipInputs(createAdminStoreMembershipRequest)) {
				adminStoreMembershipRepoHandler.addAdminStoreMembershipToRepository(
						createAdminStoreMembershipEntityFromRequest(createAdminStoreMembershipRequest));
			}
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		}
		return createAdminStoreMembershipRequest.getPlanId() + "_"
				+ createAdminStoreMembershipRequest.getStoreCategory();
	}

	public String updateAdminStoreMembership(UpdateAdminStoreMembershipRequest updateAdminStoreMembershipRequest)
			throws Exception {
		try {
			Optional<AdminStoreMembershipEntity> byPlanIdStoreCategory = adminStoreMembershipRepoHandler
					.findByPlanIdStoreCategory(updateAdminStoreMembershipRequest.getPlanIdStorecategory());
			if (!byPlanIdStoreCategory.isPresent()) {
				throw new ServiceException("Admin Store Membership Not Found.");
			}
			adminStoreMembershipRepoHandler.addAdminStoreMembershipToRepository(
					updateAdminStoreMembershipEntityFromRequest(byPlanIdStoreCategory.get(),
							updateAdminStoreMembershipRequest));
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		}
		return updateAdminStoreMembershipRequest.getPlanIdStorecategory();
	}

	public List<AdminStoreMembershipEntity> getAllAdminStoreMembership(String category, String planId,
			String noOfDays) {
		List<AdminStoreMembershipEntity> storeMembershipList = storeHandler.getAllAdminStoreMembership();
		return storeMembershipList.stream()
				.filter(entity -> ((planId == null || entity.getPlanId().equals(planId))
						&& (category == null || entity.getStoreCategory() == category)
						&& (noOfDays == null || entity.getNoOfDays() == noOfDays)))
				.collect(Collectors.toList());
	}

	boolean validateAddStoreMembershipInputs(CreateAdminStoreMembershipRequest request) throws Exception {
		boolean isStoreMembershipPresent = adminStoreMembershipRepoHandler
				.isStoreMembershipPresent(request.getPlanId() + "_" + request.getStoreCategory());
		if (isStoreMembershipPresent) {
			throw new ServiceException("Admin Store Membership Already Exists");
		}
		return true;
	}

	private AdminStoreMembershipEntity createAdminStoreMembershipEntityFromRequest(
			CreateAdminStoreMembershipRequest request) {
		AdminStoreMembershipEntity adminStoreMembershipEntity = new AdminStoreMembershipEntity();
		adminStoreMembershipEntity.setPlanId(request.getPlanId());
		adminStoreMembershipEntity.setPricePerUser(request.getPricePerUser());
		adminStoreMembershipEntity.setStoreCategory(request.getStoreCategory());
		adminStoreMembershipEntity.setState(request.getState());
		adminStoreMembershipEntity.setDistrict(request.getDistrict());
		adminStoreMembershipEntity.setNoOfDays(request.getNoOfDays());
		adminStoreMembershipEntity.setComment(Objects.nonNull(request.getComment()) ? request.getComment() : "");
		adminStoreMembershipEntity.setStatus(request.getStatus());
		adminStoreMembershipEntity.setPlanIdStoreCategory(request.getPlanId() + "_" + request.getStoreCategory());
		adminStoreMembershipEntity.setUpdatedBy("admin");
		adminStoreMembershipEntity.setStatusUpdateDate(LocalDateTime.now());
		return adminStoreMembershipEntity;
	}

	private AdminStoreMembershipEntity updateAdminStoreMembershipEntityFromRequest(
			AdminStoreMembershipEntity adminStoreMembershipEntity, UpdateAdminStoreMembershipRequest request) {
		adminStoreMembershipEntity.setPricePerUser(request.getAmount());
		adminStoreMembershipEntity.setNoOfDays(request.getNoOfDays());
		adminStoreMembershipEntity.setPriceUpdateDate(LocalDateTime.now());
		return adminStoreMembershipEntity;
	}
}
