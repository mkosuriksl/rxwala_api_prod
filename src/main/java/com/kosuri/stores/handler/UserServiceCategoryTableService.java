package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.dao.UserServiceCatgoryTable;
import com.kosuri.stores.dao.UserServiceCatgoryTableRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.UserServiceCatgoryTableDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class UserServiceCategoryTableService {

	@Autowired
	private ModelMapper modelMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserServiceCatgoryTableRepository userServiceCatgoryTableRepository;

	@Autowired
	TabStoreRepository tabStoreRepository;

	public UserServiceCatgoryTableDto createUserServiceCatgoryTableDto(
			UserServiceCatgoryTableDto userServiceCatgoryTableDto) {
		UserServiceCatgoryTable userServiceCatgoryTable = dtoToUserServiceCatgoryTable(userServiceCatgoryTableDto);
		UserServiceCatgoryTable savedUserServiceCatgoryTable = userServiceCatgoryTableRepository
				.save(userServiceCatgoryTable);
		return userServiceCatgoryTableToDto(savedUserServiceCatgoryTable);

	}

	private UserServiceCatgoryTableDto userServiceCatgoryTableToDto(UserServiceCatgoryTable userServiceCatgoryTable) {
		UserServiceCatgoryTableDto userServiceCatgoryTableDto = modelMapper.map(userServiceCatgoryTable,
				UserServiceCatgoryTableDto.class);
		return userServiceCatgoryTableDto;
	}

	private UserServiceCatgoryTable dtoToUserServiceCatgoryTable(
			UserServiceCatgoryTableDto userServiceCatgoryTableDto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> registrationUser = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (registrationUser.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to registration users only.");
		}
		UserServiceCatgoryTable userServiceCatgoryTable = modelMapper.map(userServiceCatgoryTableDto,
				UserServiceCatgoryTable.class);
		userServiceCatgoryTable.setUpdatedBy(registrationUser.get().getUserId());
		return userServiceCatgoryTable;
	}

//	public UserServiceCatgoryTableDto updateUserServiceCatgoryTable(UserServiceCatgoryTableDto dto) {
//
//		UserServiceCatgoryTable userServiceCatgoryTable = userServiceCatgoryTableRepository
//				.findByUserId(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException(
//						"UserServiceCatgory not found for cabGenId: " + dto.getUserId()));
//		userServiceCatgoryTable.setServiceCategories(dto.getServiceCategories());
//		UserServiceCatgoryTable updatedUserServiceCatgoryTable = userServiceCatgoryTableRepository
//				.save(userServiceCatgoryTable);
//		return userServiceCatgoryTableToDto(updatedUserServiceCatgoryTable);
//	}
	
	public UserServiceCatgoryTableDto updateUserServiceCatgoryTable(UserServiceCatgoryTableDto dto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> registrationUser = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (registrationUser.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to registration users only.");
		}
	    // First check if the user exists in tab_store_user_login
	    Optional<TabStoreUserEntity> storeUserOpt = tabStoreRepository.findByUserId(dto.getUserId());
	    if (storeUserOpt.isEmpty()) {
	        throw new ResourceNotFoundException("User not found in store user table for userId: " + dto.getUserId());
	    }

	    // Then check if user already has a category entry
	    Optional<UserServiceCatgoryTable> existingCategoryOpt = userServiceCatgoryTableRepository.findByUserId(dto.getUserId());

	    UserServiceCatgoryTable userServiceCatgoryTable;
	    if (existingCategoryOpt.isPresent()) {
	        userServiceCatgoryTable = existingCategoryOpt.get();
	        userServiceCatgoryTable.setServiceCategories(dto.getServiceCategories());
			userServiceCatgoryTable.setUpdatedBy(registrationUser.get().getUserId());

	    } else {
	        // Create new entry if not exists
	        userServiceCatgoryTable = new UserServiceCatgoryTable();
	        userServiceCatgoryTable.setUserId(dto.getUserId());
			userServiceCatgoryTable.setUpdatedBy(registrationUser.get().getUserId());
	        userServiceCatgoryTable.setServiceCategories(dto.getServiceCategories());
	    }

	    UserServiceCatgoryTable saved = userServiceCatgoryTableRepository.save(userServiceCatgoryTable);
	    return userServiceCatgoryTableToDto(saved);
	}


//	public List<UserServiceCatgoryTableDto> findAll() {
//        List<UserServiceCatgoryTable> entities = userServiceCatgoryTableRepository.findAll();
//        return entities.stream().map(this::userServiceCatgoryTableToDto).toList();
//    }

	public List<UserServiceCatgoryTable> findAll(String userId, Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("userId");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserServiceCatgoryTable> query = cb.createQuery(UserServiceCatgoryTable.class);
		Root<UserServiceCatgoryTable> root = query.from(UserServiceCatgoryTable.class);
		List<Predicate> predicates = new ArrayList<>();

		if (userId != null) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}
}
