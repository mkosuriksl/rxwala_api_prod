package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AdminEntity;
import com.kosuri.stores.dao.AdminRepository;
import com.kosuri.stores.dao.AdminStoreCategory;
import com.kosuri.stores.dao.AdminStoreCategoryRepo;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.AdminStoreCategoryDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class AdminStoreCategoryService {

	@Autowired
	private ModelMapper modelMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AdminStoreCategoryRepo adminStoreCategoryRepo;

	@Autowired
	TabStoreRepository tabStoreRepository;

	@Autowired
	private AdminRepository adminRepository;

	public AdminStoreCategoryDto createAdminStoreCategory(AdminStoreCategoryDto adminStoreCategoryDto) {
		AdminStoreCategory adminStoreCategory = dtoToadminStoreCategory(adminStoreCategoryDto);
		AdminStoreCategory saveddtItemList = adminStoreCategoryRepo.save(adminStoreCategory);
		return adminStoreCategoryToDto(saveddtItemList);

	}

	private AdminStoreCategoryDto adminStoreCategoryToDto(AdminStoreCategory adminStoreCategory) {
		AdminStoreCategoryDto adminStoreCategoryDto = modelMapper.map(adminStoreCategory, AdminStoreCategoryDto.class);
		return adminStoreCategoryDto;
	}

	private AdminStoreCategory dtoToadminStoreCategory(AdminStoreCategoryDto adminStoreCategoryDto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<AdminEntity> login = adminRepository.findByEmailId(loggedInUserEmail);
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		AdminStoreCategory adminStoreCategory = modelMapper.map(adminStoreCategoryDto, AdminStoreCategory.class);
		String updatedBy = null;
		if (login.isPresent()) {
			updatedBy = login.get().getUpdatedBy();

		} else if (loginStore.isPresent()) {
			updatedBy = loginStore.get().getUserId();

		}
		adminStoreCategory.setStatus("active");
		adminStoreCategory.setUpdatedBy(updatedBy);
		return adminStoreCategory;
	}

	public AdminStoreCategoryDto updateAdminStoreCategory(AdminStoreCategoryDto dto) {

		AdminStoreCategory adminStoreCategory = adminStoreCategoryRepo.findByStoreCategoryId(dto.getStoreCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("store Category not found : " + dto.getStoreCategoryId()));
		adminStoreCategory.setStatus(dto.getStatus());
		adminStoreCategory.setStatusUpdatedDate(LocalDateTime.now());
		AdminStoreCategory updatedAdminStoreCategory = adminStoreCategoryRepo.save(adminStoreCategory);
		return adminStoreCategoryToDto(updatedAdminStoreCategory);
	}

	public List<AdminStoreCategory> getAllAdminStoreCategories() {
        return adminStoreCategoryRepo.findAll();
    }
}
