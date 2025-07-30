package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.AdminEntity;
import com.kosuri.stores.dao.AdminRepository;
import com.kosuri.stores.dao.StoreAdminBrandEntity;
import com.kosuri.stores.dao.StoreAdminBrandRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.model.dto.StoreAdminBrandRequest;
import com.kosuri.stores.utils.CurrentUser;

@Service
public class StoreAdminBrandHandler {

	@Autowired
	private StoreAdminBrandRepository storeAdminBrandRepository;

	@Autowired
	private TabStoreRepository storeRepository;

	@Transactional
	public List<StoreAdminBrandEntity> getStoreAdminBrands() {
		return storeAdminBrandRepository.findAll();
	}

	@Transactional
	public StoreAdminBrandEntity getStoreAdminBrandById(String schemeId) {
		return storeAdminBrandRepository.findById(schemeId).orElse(null);
	}

	@Transactional
	public Map<String, Object> saveStoreAdminBrand(String adminEamil, StoreAdminBrandRequest storeAdminBrand) {
		TabStoreUserEntity admin = storeRepository.findByStoreUserEmail(adminEamil).orElse(null);
		StoreAdminBrandEntity sab = new StoreAdminBrandEntity();
		sab.setId(admin.getUserId() + storeAdminBrand.getBrandId());
		sab.setUpdatedBy(admin.getStoreAdminEmail());
		sab = mapToEntity(sab, storeAdminBrand);
		storeAdminBrandRepository.save(sab);
		return Map.of("statusCode", HttpStatus.CREATED + " ", "isSuccess", Boolean.TRUE, "message",
				"data has been saved successfully!");
	}

	@Transactional
	public Map<String, Object> updateStoreAdminBrand(String id, StoreAdminBrandRequest updateCategory) {
		StoreAdminBrandEntity sab = storeAdminBrandRepository.findById(id).orElse(null);
		mapToEntity(sab, updateCategory);
		return Map.of("statusCode", HttpStatus.NO_CONTENT + " ", "isSuccess", Boolean.TRUE, "message",
				"data has been updated successfully!");
	}

	private StoreAdminBrandEntity mapToEntity(StoreAdminBrandEntity sab, StoreAdminBrandRequest req) {
		sab.setBrandName(req.getBrandName());
		sab.setBrandId(req.getBrandId());
		sab.setUpdatedDate(LocalDate.now());
		sab.setStoreId(req.getStoreId());
		sab.setItemCategory(req.getItemCategory());
		sab.setItemSubcategory(req.getItemSubcategory());
		return sab;
	}

	@Transactional
	public Map<String, Object> deleteStoreAdminBrand(String schemeId) {
		StoreAdminBrandEntity sab = storeAdminBrandRepository.findById(schemeId).orElse(null);
		storeAdminBrandRepository.delete(sab);
		return Map.of("statusCode", HttpStatus.NO_CONTENT + " ", "isSuccess", Boolean.TRUE, "message",
				"data has been deleted successfully!");
	}
}
