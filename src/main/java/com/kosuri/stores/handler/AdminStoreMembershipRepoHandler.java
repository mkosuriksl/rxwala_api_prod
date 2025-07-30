package com.kosuri.stores.handler;

import com.kosuri.stores.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminStoreMembershipRepoHandler {

	@Autowired
	private AdminStoreMembershipRepository adminStoreMembershipRepository;

	public boolean isStoreMembershipPresent(String planIdStoreCategory) {
		Optional<AdminStoreMembershipEntity> byPlanIdStoreCategory = adminStoreMembershipRepository
				.findByPlanIdStoreCategory(planIdStoreCategory);
		return byPlanIdStoreCategory.isPresent();
	}

	public AdminStoreMembershipEntity addAdminStoreMembershipToRepository(
			AdminStoreMembershipEntity adminStoreMembershipEntity) {
		return adminStoreMembershipRepository.save(adminStoreMembershipEntity);
	}

	public Optional<AdminStoreMembershipEntity> findByPlanIdStoreCategory(String planIdStoreCategory) {
		return adminStoreMembershipRepository.findByPlanIdStoreCategory(planIdStoreCategory);
	}

}
