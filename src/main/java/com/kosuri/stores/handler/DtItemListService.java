package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AdminEntity;
import com.kosuri.stores.dao.AdminRepository;
import com.kosuri.stores.dao.DtItemList;
import com.kosuri.stores.dao.DtItemListRepo;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.DtItemListDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DtItemListService {

	@Autowired
	private ModelMapper modelMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DtItemListRepo dtItemListRepo;

	@Autowired
	TabStoreRepository tabStoreRepository;

	@Autowired
	private AdminRepository adminRepository;

	public DtItemListDto createDtItemList(DtItemListDto dtItemListDto) {
		DtItemList dtItemList = dtoToDtItemList(dtItemListDto);
		DtItemList saveddtItemList = dtItemListRepo.save(dtItemList);
		return dtItemListToDto(saveddtItemList);

	}

	private DtItemListDto dtItemListToDto(DtItemList dtItemList) {
		DtItemListDto dtItemListDto = modelMapper.map(dtItemList, DtItemListDto.class);
		return dtItemListDto;
	}

	private DtItemList dtoToDtItemList(DtItemListDto dtItemListDto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<AdminEntity> login = adminRepository.findByEmailId(loggedInUserEmail);
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		DtItemList dtItemList = modelMapper.map(dtItemListDto, DtItemList.class);
		String updatedBy = null;
		if (login.isPresent()) {
			updatedBy = login.get().getUpdatedBy();

		} else if (loginStore.isPresent()) {
			updatedBy = loginStore.get().getUserId();

		}
		dtItemList.setUserIdItemCode(updatedBy + "_" + dtItemListDto.getItemCode());
		dtItemList.setUpdatedBy(updatedBy);
		return dtItemList;
	}

	public DtItemListDto updateDtItemList(DtItemListDto dto) {

		DtItemList dtItemList = dtItemListRepo.findByItemCode(dto.getItemCode())
				.orElseThrow(() -> new ResourceNotFoundException("Item code not found : " + dto.getItemCode()));
		dtItemList.setItemname(dto.getItemname());
		DtItemList updatedDtItemList = dtItemListRepo.save(dtItemList);
		return dtItemListToDto(updatedDtItemList);
	}

	public List<DtItemList> findAll(String itemCategory, String itemSubcategory, String itemCode, String brand,
			String manufacturer, Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("itemCategory", "itemSubcategory", "itemCode", "brand",
				"manufacturer");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DtItemList> query = cb.createQuery(DtItemList.class);
		Root<DtItemList> root = query.from(DtItemList.class);
		List<Predicate> predicates = new ArrayList<>();

		if (itemCategory != null) {
			predicates.add(cb.equal(root.get("itemCategory"), itemCategory));
		}
		if (itemSubcategory != null) {
			predicates.add(cb.equal(root.get("itemSubcategory"), itemSubcategory));
		}
		if (itemCode != null) {
			predicates.add(cb.equal(root.get("itemCode"), itemCode));
		}
		if (brand != null) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (manufacturer != null) {
			predicates.add(cb.equal(root.get("manufacturer"), manufacturer));
		}

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}
}
