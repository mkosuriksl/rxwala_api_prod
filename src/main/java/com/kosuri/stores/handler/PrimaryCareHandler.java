package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.PrimaryCareAvailabilityHistoryEntity;
import com.kosuri.stores.dao.PrimaryCareAvailabilityHistoryRepository;
import com.kosuri.stores.dao.PrimaryCareAvailabilityLatestEntity;
import com.kosuri.stores.dao.PrimaryCareAvailabilityLatestRepository;
import com.kosuri.stores.dao.PrimaryCareCenterRepository;
import com.kosuri.stores.dao.PrimaryCareEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.PrimaryCareDTOWithoutSec;
import com.kosuri.stores.model.request.PrimaryCareAvailabilityRequest;
import com.kosuri.stores.model.request.PrimaryCareServiceRequest;
import com.kosuri.stores.model.request.PrimaryCareUserRequest;
import com.kosuri.stores.model.request.UpdatePrimaryCareServicesRequest;
import com.kosuri.stores.model.request.UpdatePrimaryCareServicesResponse;
import com.kosuri.stores.model.request.UpdatePrimaryCareUserRequest;
import com.kosuri.stores.model.request.UpdateServicesRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllPrimaryCareCentersResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class PrimaryCareHandler {

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private PrimaryCareCenterRepository primaryCareCenterRepository;

	@Autowired
	private PrimaryCareAvailabilityHistoryRepository historyRepository;

	@Autowired
	private PrimaryCareAvailabilityLatestRepository latestRepository;

	@Autowired
	private StoreHandler storeHandler;

	@Autowired
	private TabStoreRepository storeRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private StoreRepository storeRepo;

	public GenericResponse addPrimaryCareServices(PrimaryCareServiceRequest serviceRequest, String loggedInUserEmail) {

		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail).get();
		GenericResponse response = new GenericResponse();
		List<PrimaryCareEntity> services = serviceRequest.getServices().stream().map(serv -> {
			PrimaryCareEntity primaryCareEntity = new PrimaryCareEntity();
//			primaryCareEntity.setUserServiceId(serviceRequest.getStoreName() + "_" + serv.getServiceId());
			primaryCareEntity.setUserIdStoreIdServiceId(user.getUserId() + "_" + serviceRequest.getStoreName()+ "_"+ serv.getServiceId());
			primaryCareEntity.setUpdatedBy(user.getUserId());
			primaryCareEntity.setServiceId(serv.getServiceId());
			primaryCareEntity.setServiceName(serv.getServiceName());
			primaryCareEntity.setStoreId(serviceRequest.getStoreName());
			primaryCareEntity.setUserId(user.getUserId());
			primaryCareEntity.setUserIdStoreId(user.getUserId() + "_" + serviceRequest.getStoreName());
			if (serv.getPrice() != 0.0) {  
			    primaryCareEntity.setPrice(serv.getPrice());
			    primaryCareEntity.setAmountUpdatedDate(LocalDateTime.now());
			}
			return primaryCareEntity;
		}).toList();
		primaryCareCenterRepository.saveAll(services);
		response.setResponseMessage("Primary Care Services Added Successfully. Number Of Services Added:: "
				+ serviceRequest.getServices().size());
		return response;
	}

	public GenericResponse getPrimaryCareServices(String loggedInUserEmail, String storeId, String serviceId,
			String location) {
		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail).get();
		GenericResponse gr = new GenericResponse();
		List<PrimaryCareEntity> list = primaryCareCenterRepository.findByUpdatedByAndStoreIdAndServiceId(user.getUserId(),storeId,serviceId);
		gr.setResponseMessage("Primary care centers successfully retrieved.");
		gr.setDetails(list);
		return gr;
	}

	public List<PrimaryCareDTOWithoutSec> getPrimaryCareServicesWithoutSec(String userIdStoreId,String userId,String serviceId,Map<String, String> requestParams) {
		
		List<String> expectedParams = Arrays.asList("userIdStoreId","userId","serviceId");
	    for (String paramName : requestParams.keySet()) {
	        if (!expectedParams.contains(paramName)) {
	            throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
	        }
	    }
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PrimaryCareEntity> query = cb.createQuery(PrimaryCareEntity.class);
		Root<PrimaryCareEntity> root = query.from(PrimaryCareEntity.class);
		List<Predicate> predicates = new ArrayList<>();
		
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		if (userId != null) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (serviceId != null) {
			predicates.add(cb.equal(root.get("serviceId"), serviceId));
		}
//		if (location != null) {
//			predicates.add(cb.equal(root.get("location"), location));
//		}
		
		query.where(predicates.toArray(new Predicate[0]));

		List<PrimaryCareEntity> primaryCareEntities = entityManager.createQuery(query).getResultList();
		
		  return primaryCareEntities.stream().map(entity -> new PrimaryCareDTOWithoutSec(
		            entity.getUserIdStoreIdServiceId(), 
		            entity.getServiceId(),
		            entity.getServiceName(),
		            entity.getPrice(),
		            entity.getDescription(),
		            entity.getUserId(),
		            entity.getUserIdStoreId(),
		            entity.getServiceCategory()
		    )).collect(Collectors.toList());
	}
	
	public UpdatePrimaryCareServicesResponse updatePrimaryCareCenterServices(UpdateServicesRequest request, String loggedInUserEmail) {
	    // Fetch the logged-in user
	    TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    // Find the existing PrimaryCareEntity by ID
	    PrimaryCareEntity primaryCareEntity = primaryCareCenterRepository.findById(request.getUserIdStoreIdServiceId())
	            .orElseThrow(() -> new ResourceNotFoundException("Primary Care Service Not Found for ID: " + request.getUserIdStoreIdServiceId()));

	    StoreEntity store = storeRepo.findById(request.getStoreId())
	    	    .orElseThrow(() -> new ResourceNotFoundException("Store not found for ID: " + request.getStoreId()));

	    // Update service details
	    primaryCareEntity.setUpdatedBy(user.getUserId());
	    primaryCareEntity.setServiceId(request.getServiceId());
	    primaryCareEntity.setServiceName(request.getServiceName());
	    primaryCareEntity.setStoreId(request.getStoreId());

	    // Update price only if it's not null
	    if (request.getPrice() != 0.0 && request.getPrice() != 0.0) {
	        primaryCareEntity.setPrice(request.getPrice());
	        primaryCareEntity.setAmountUpdatedDate(LocalDateTime.now());
	    }

	    // Save the updated record
	    primaryCareCenterRepository.save(primaryCareEntity);

	    // Return response with a single object
	    return new UpdatePrimaryCareServicesResponse("Service updated successfully", primaryCareEntity);
	}

	
//	public UpdatePrimaryCareServicesResponse updatePrimaryCareCenterServices(List<UpdateServicesRequest> requests, String loggedInUserEmail) {
//	    TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail)
//	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//	    List<PrimaryCareEntity> services = requests.stream().map(serv -> {
//	        PrimaryCareEntity primaryCareEntity = primaryCareCenterRepository.findById(serv.getUserIdStoreIdServiceId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Primary Care Service Not Found"));
//
//	        primaryCareEntity.setUpdatedBy(user.getUserId());
//	        primaryCareEntity.setServiceId(serv.getServiceId());
//	        primaryCareEntity.setServiceName(serv.getServiceName());
////	        primaryCareEntity.setUserIdStoreId(user.getUserId() + "_" + requests.getStoreName());
//
//	        if (serv.getPrice() != 0.0) {
//	            primaryCareEntity.setPrice(serv.getPrice());
//	            primaryCareEntity.setAmountUpdatedDate(LocalDateTime.now());
//	        }
//	        return primaryCareEntity;
//	    }).toList();
//
//	    primaryCareCenterRepository.saveAll(services);
//	    return new UpdatePrimaryCareServicesResponse("Services updated successfully", services);
//	}

//	public String updatePrimaryCareCenterServices(UpdatePrimaryCareServicesRequest requests, String loggedInUserEmail) {
//		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail).get();
//		List<PrimaryCareEntity> services = requests.getServices().stream().map(serv -> {
//			PrimaryCareEntity primaryCareEntity = primaryCareCenterRepository.findById(serv.getUserIdStoreIdServiceId())
//					.orElseThrow(() -> new ResourceNotFoundException("Primary Care Service Not Found"));
//			primaryCareEntity.setUpdatedBy(user.getUserId());
//			primaryCareEntity.setServiceId(serv.getServiceId());
//			primaryCareEntity.setServiceName(serv.getServiceName());
////			primaryCareEntity.setStoreId(requests.getStoreName());
//			primaryCareEntity.setUserIdStoreId(user.getUserId() + "_" + requests.getStoreName());
//			if (serv.getPrice() != 0.0) {
//				primaryCareEntity.setPrice(serv.getPrice());
//				primaryCareEntity.setAmountUpdatedDate(LocalDateTime.now());
//			}
//			return primaryCareEntity;
//		}).toList();
//		primaryCareCenterRepository.saveAll(services);
//		return "updated";
//	}
	
//	public UpdatePrimaryCareServicesResponse updatePrimaryCareCenterServices(UpdatePrimaryCareServicesRequest requests, String loggedInUserEmail) {
//	    TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail)
//	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//	    List<PrimaryCareEntity> services = requests.getServices().stream().map(serv -> {
//	        PrimaryCareEntity primaryCareEntity = primaryCareCenterRepository.findById(serv.getUserIdStoreIdServiceId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Primary Care Service Not Found"));
//
//	        primaryCareEntity.setUpdatedBy(user.getUserId());
//	        primaryCareEntity.setServiceId(serv.getServiceId());
//	        primaryCareEntity.setServiceName(serv.getServiceName());
//	        primaryCareEntity.setUserIdStoreId(user.getUserId() + "_" + requests.getStoreName());
//
//	        if (serv.getPrice() != 0.0) {
//	            primaryCareEntity.setPrice(serv.getPrice());
//	            primaryCareEntity.setAmountUpdatedDate(LocalDateTime.now());
//	        }
//	        return primaryCareEntity;
//	    }).toList();
//
//	    primaryCareCenterRepository.saveAll(services);
//	    return new UpdatePrimaryCareServicesResponse("Services updated successfully", services);
//	}


	@Transactional
	public GenericResponse addPrimaryCare(List<PrimaryCareUserRequest> requests, String loggedInUserEmail)
			throws Exception {
		GenericResponse response = new GenericResponse();
		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail)
				.orElseThrow(() -> new Exception("Store User Found"));

		for (PrimaryCareUserRequest request : requests) {
			if (!repositoryHandler.isPCActive(request)) {
				throw new Exception("Primary Care Center Cannot Be Added As The Store Is Not Verified.");
			}

			PrimaryCareEntity primaryCareEntity = setEntityFromPrimaryCareRequest(request, true);
			primaryCareEntity.setUserId(user.getUserId());
//			primaryCareEntity.setUserServiceId(user.getUserId() + "_" + request.getServiceId());
			primaryCareEntity.setUserIdStoreId(user.getUserId() + "_" + request.getStoreId());
			primaryCareEntity.setUserIdStoreIdServiceId(user.getUserId() + "_" + request.getStoreId()+ "_"+ request.getServiceId());
			primaryCareEntity.setUpdatedBy(user.getUserId());
			try {
				if (repositoryHandler.addPrimaryCareCenter(primaryCareEntity)) {
				} else {
					throw new APIException("Unable To Add Primary Care Service..Issue While Adding "
							+ request.getServiceId() + " Service");
				}
			} catch (DataIntegrityViolationException e) {
				throw new Exception(e.getCause().getCause().getMessage());
			}
			response.setResponseMessage(
					"Primary Care Services Added Successfully. Number Of Services Added:: " + requests.size());
		}
		return response;
	}

	private PrimaryCareEntity setEntityFromPrimaryCareRequest(PrimaryCareUserRequest request, boolean isPcActive) {
		PrimaryCareEntity primaryEntity = new PrimaryCareEntity();
		primaryEntity.setStoreId(request.getStoreId());
		primaryEntity.setServiceId(request.getServiceId());
		primaryEntity.setServiceName(request.getServiceName());
		primaryEntity.setServiceCategory(request.getServiceCategory());
		primaryEntity.setDescription(request.getDescription());
		primaryEntity.setStatus(isPcActive ? "1" : "0");
		primaryEntity.setPrice(request.getPrice());
		return primaryEntity;
	}

	@Transactional
	public String updatePrimaryCareCenter(List<UpdatePrimaryCareUserRequest> requests, String loggedInUserEmail)
			throws Exception {
		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail)
				.orElseThrow(() -> new Exception("Store User Found"));

		requests.stream().forEach(request -> {

			PrimaryCareEntity serviceEntity = primaryCareCenterRepository
					.findByUserIdStoreIdServiceId(request.getUserIdStoreIdServiceId()).orElseThrow(
							() -> new RuntimeException("Primary Care not Found By Id : " + request.getUserIdStoreIdServiceId()));

			// Check and update the price, status, and timestamps
			serviceEntity.setServiceId(serviceEntity.getServiceId());
			serviceEntity.setStoreId(serviceEntity.getStoreId());
			serviceEntity.setServiceCategory(request.getServiceCategory());
			serviceEntity.setServiceName(request.getServiceName());
			serviceEntity.setDescription(request.getDescription());
			serviceEntity.setUpdatedBy(user.getUserId());
			if (request.getPrice() != 0.0) {
				serviceEntity.setPrice(request.getPrice());
				serviceEntity.setAmountUpdatedDate((LocalDateTime.now()));
			}
			if (!repositoryHandler.isPCActive(request.getStoreId())) {
				serviceEntity.setStatus(repositoryHandler.isPCActive(request.getStoreId()) ? "1" : "0");
				serviceEntity.setStatusUpdatedDate(LocalDateTime.now());
			}
			// Save the updated entity
			// repositoryHandler.savePrimaryServiceEntity(serviceEntity);
		});
		return "Primary Care Services Updated Successfully. Number Of Services Updated :: " + requests.size();
	}

	public GetAllPrimaryCareCentersResponse getAllPrimaryCareCenters(String loggedInUserEmail) {
		GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();
		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(loggedInUserEmail)
				.orElseThrow(() -> new RuntimeException("LoggedIn User Found"));
		List<PrimaryCareEntity> primaryCareEntities = primaryCareCenterRepository.findAll().stream()
				.filter(primaryCareCenter -> primaryCareCenter.getUserId().equals(user.getUserId()))
				.collect(Collectors.toList());
		response.setPrimaryCareCenters(primaryCareEntities);
		if (primaryCareEntities.isEmpty()) {
			response.setResponseMessage("No primary care centers found for the specified user.");
		} else {
			response.setResponseMessage("Primary care centers successfully retrieved.");
		}
		return response;
	}

	public GetAllPrimaryCareCentersResponse getPrimaryCareCenterByLocationOrUserId(String location, String userId,
			String storeId) {
		GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();

		List<PrimaryCareEntity> primaryCareCenters = new ArrayList<>();

		if (storeId != null && userId != null) {
			primaryCareCenters = primaryCareCenterRepository.findByStoreIdAndUserId(storeId, userId);
			response.setResponseMessage("Primary Care Center Fetched Successfully by StoreId and User ID");
		} else if (storeId != null) {
			List<String> storeIds = new ArrayList<>();
			storeIds.add(storeId);
			getPrimaryCareEntityUsingStoreIds(storeIds, primaryCareCenters);
			response.setResponseMessage("Primary Care Centers Fetched Successfully by StoreId");
		} else if (location != null && !location.isEmpty()) {
			List<String> storeIds = storeHandler.getStoreIdFromLocation(location);
			getPrimaryCareCentreUsingStoreIds(storeIds, primaryCareCenters);
			response.setResponseMessage("Diagnostic Centers Fetched Successfully by Location");
		} else if (userId != null && !userId.isEmpty()) {
			primaryCareCenters = primaryCareCenterRepository.findByUserId(userId);
			response.setResponseMessage("Diagnostic Centers Fetched Successfully by User ID");
		} else {
			response.setResponseMessage("No location, StoreId and user ID provided to fetch Primary Care Centers");
			return response;
		}
		response.setPrimaryCareCenters(primaryCareCenters);
		return response;
	}

	private void getPrimaryCareEntityUsingStoreIds(List<String> storeIds, List<PrimaryCareEntity> primaryCareCenters) {
		for (String storeId : storeIds) {
			List<PrimaryCareEntity> primaryCareEntities = primaryCareCenterRepository.findByStoreId(storeId);
			primaryCareCenters.addAll(primaryCareEntities);
		}
	}

	private void getPrimaryCareCentreUsingStoreIds(List<String> storeIds, List<PrimaryCareEntity> primaryCareCenters) {
		for (String storeId : storeIds) {
			List<PrimaryCareEntity> primaryCareCenterList = primaryCareCenterRepository.findByStoreId(storeId);
			primaryCareCenters.addAll(primaryCareCenterList);
		}
	}

	@Transactional
	public void updatePrimaryCareAvailability(PrimaryCareAvailabilityRequest dto) {

		PrimaryCareAvailabilityHistoryEntity historyRecord = new PrimaryCareAvailabilityHistoryEntity();
		historyRecord.setProviderId(dto.getProviderId());
		historyRecord.setPrimaryCareAvailLoc(dto.getPrimaryCareAvailLoc());
		historyRecord.setUpdatedDate(LocalDate.now());
		historyRecord.setUpdatedBy(dto.getUpdatedBy());
		historyRecord.setAvailability(dto.getAvailability());
		historyRepository.save(historyRecord);

		PrimaryCareAvailabilityLatestEntity latestRecord = latestRepository.findById(dto.getProviderId())
				.map(existingLatest -> {
					existingLatest.setPrimaryCareAvailLoc(dto.getPrimaryCareAvailLoc());
					existingLatest.setUpdatedDate(LocalDate.now());
					existingLatest.setUpdatedBy(dto.getUpdatedBy());
					existingLatest.setAvailability(dto.getAvailability());
					return existingLatest;
				}).orElseGet(() -> {
					PrimaryCareAvailabilityLatestEntity newLatest = new PrimaryCareAvailabilityLatestEntity();
					newLatest.setProviderId(dto.getProviderId());
					newLatest.setPrimaryCareAvailLoc(dto.getPrimaryCareAvailLoc());
					newLatest.setUpdatedDate(LocalDate.now());
					newLatest.setUpdatedBy(dto.getUpdatedBy());
					newLatest.setAvailability(dto.getAvailability());
					return newLatest;
				});

		latestRepository.save(latestRecord);
	}

	public List<String> getPcServiceCategoryHomeDistinct() {
		return primaryCareCenterRepository.findPcServiceCategoryName();
	}
	
	public Map<String, Object> getServiceCategoryBySearch(String serviceCategory) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
	    Root<PrimaryCareEntity> root = query.from(PrimaryCareEntity.class);

	    List<Predicate> predicates = new ArrayList<>();
	    if (serviceCategory != null && !serviceCategory.isBlank()) {
	        predicates.add(cb.like(cb.lower(root.get("serviceCategory")), "%" + serviceCategory.toLowerCase() + "%"));
	    }

	    query.multiselect(root.get("serviceCategory"));
	    query.where(cb.and(predicates.toArray(new Predicate[0])));

	    List<Object[]> resultList = entityManager.createQuery(query).getResultList();

	    List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("serviceCategory", obj[0]);
	        return map;
	    }).collect(Collectors.toList());

	    return Map.of(
	        "message", "serviceCategory fetched successfully",
	        "status", true,
	        "totalCount", responseData.size(), // ✅ Total count added
	        "data", responseData
	    );
	}
}
