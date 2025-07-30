package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.DCPackageDetails;
import com.kosuri.stores.dao.DCPackageDetailsHistory;
import com.kosuri.stores.dao.DCPackageDetailsHistoryRepository;
import com.kosuri.stores.dao.DCPackageDetailsRepository;
import com.kosuri.stores.dao.DCPackageHeader;
import com.kosuri.stores.dao.DCPackageHeaderHistory;
import com.kosuri.stores.dao.DCPackageHeaderHistoryRepository;
import com.kosuri.stores.dao.DCPackageHeaderRepository;
import com.kosuri.stores.dao.DiagnosticServiceRepository;
import com.kosuri.stores.dao.DiagnosticServicesEntity;
import com.kosuri.stores.dao.PrimaryCareCenterRepository;
import com.kosuri.stores.dao.PrimaryCareEntity;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.DiagnosticServiceDto;
import com.kosuri.stores.model.dto.PackageRequestDto;
import com.kosuri.stores.model.dto.PackageRequestDto1;
import com.kosuri.stores.model.dto.PrimaryCareDto;
import com.kosuri.stores.model.dto.ServiceDto;
import com.kosuri.stores.model.dto.ServiceDto1;
import com.kosuri.stores.model.dto.StoreDto;
import com.kosuri.stores.model.dto.StoreWithDiagnosticServiceResponseDto;
import com.kosuri.stores.model.dto.StoreWithPrimaryCareResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DCPackageService {

	@Autowired
	private DCPackageHeaderRepository packageHeaderRepository;

	@Autowired
	private DCPackageDetailsRepository packageDetailsRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private DCPackageHeaderHistoryRepository dcPackageHeaderHistoryRepository;

	@Autowired
	private DCPackageDetailsHistoryRepository dcPackageDetailsHistoryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DiagnosticServiceRepository diagnosticServiceRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@Autowired
	private PrimaryCareCenterRepository primaryCareCenterRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public PackageRequestDto createPackageDetails(PackageRequestDto packageRequest) {
//	    String packageId = generateUniquePackageId();
		double totalAmount = 0;
		int lineNumber = 1;

		// Validate and fetch store entity
		StoreEntity storeEntity = storeRepository.findById(packageRequest.getStoreId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"StoreId is not found for StoreEntity: " + packageRequest.getStoreId()));

		TabStoreUserEntity tabStoreUserEntity = tabStoreRepository.findByUserId(packageRequest.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"UserId is not found for Table Store UserEntity: " + packageRequest.getUserId()));

		// Iterate over selected services
		for (ServiceDto service : packageRequest.getSelectedServices()) {
			double serviceTotal = calculateTotalAmount(service); // Fix: Calculate correctly
			totalAmount += serviceTotal;

			// Create and save package details
			DCPackageDetails packageDetails = new DCPackageDetails();
			String packageIdLineId = packageRequest.getPackageId() + "_" + String.format("%03d", lineNumber++);
			System.out.println("Package ID: " + packageRequest.getPackageId());
			System.out.println("Generated PackageIdLineId: " + packageIdLineId);
			packageDetails.setPackageIdLineId(packageIdLineId);
			packageDetails.setPackageId(packageRequest.getPackageId());

			packageDetails.setServiceId(service.getServiceId());
			packageDetails.setAmount(service.getAmount());
			packageDetails.setDiscount(service.getDiscount());
			packageDetails.setUpdatedBy(service.getUpdatedBy());
			packageDetails.setServiceName(service.getServiceName());
			packageDetails.setUpdatedDate(LocalDateTime.now());
			service.setPackageIdLineId(packageIdLineId);

			packageDetailsRepository.save(packageDetails);
		}

		// Save package header
		DCPackageHeader packageHeader = new DCPackageHeader();
		packageHeader.setPackageId(packageRequest.getPackageId());
		packageHeader.setUpdatedBy(packageRequest.getUpdatedBy());
		packageHeader.setUserId(tabStoreUserEntity.getUserId());
		packageHeader.setStoreId(storeEntity.getId());
		packageHeader.setTotalAmount(totalAmount);
		packageHeader.setPackageName(packageRequest.getPackageName());
		packageHeader.setUpdatedDate(LocalDateTime.now());

		packageHeaderRepository.save(packageHeader);

		return new PackageRequestDto(packageRequest.getPackageId(), packageRequest.getPackageName(), totalAmount,
				packageRequest.getUpdatedBy(), packageRequest.getStoreId(), packageRequest.getUserId(),
				packageRequest.getSelectedServices());
	}

	// Fix: Calculate total amount correctly
	private double calculateTotalAmount(ServiceDto service) {
		return service.getAmount() - (service.getAmount() * service.getDiscount() / 100);
	}

//	@Transactional
//	public PackageRequestDto updatePackageDetails(PackageRequestDto packageRequestDto) {
//	    String packageId = packageRequestDto.getPackageId();
//
//	    // Fetch existing package header
//	    DCPackageHeader existingHeader = packageHeaderRepository.findById(packageId)
//	            .orElseThrow(() -> new ResourceNotFoundException("Package ID not found: " + packageId));
//
//	    // Move existing header to history
//	    DCPackageHeaderHistory headerHistory = new DCPackageHeaderHistory();
//	    BeanUtils.copyProperties(existingHeader, headerHistory);
//	    dcPackageHeaderHistoryRepository.save(headerHistory);
//
//	    // Move existing package details to history
//	    List<DCPackageDetails> existingDetails = packageDetailsRepository.findByPackageId(packageId);
//	    for (DCPackageDetails existingDetail : existingDetails) {
//	        DCPackageDetailsHistory detailsHistory = new DCPackageDetailsHistory();
//	        BeanUtils.copyProperties(existingDetail, detailsHistory);
//	        dcPackageDetailsHistoryRepository.save(detailsHistory);
//	    }
//
//	    // Calculate new totalAmount based on the request's selectedServices
//	    double totalAmount = packageRequestDto.getSelectedServices().stream()
//	            .mapToDouble(service -> service.getAmount() - (service.getAmount() * service.getDiscount() / 100))
//	            .sum();
//
//	    // Update package header with new totalAmount
//	    existingHeader.setTotalAmount(totalAmount);
//	    existingHeader.setUpdatedBy(packageRequestDto.getUpdatedBy());
//	    existingHeader.setUpdatedDate(LocalDateTime.now());
//	    packageHeaderRepository.save(existingHeader);
//
//	    // Set updated totalAmount in response DTO
//	    packageRequestDto.setTotalAmount(totalAmount);
//
//	    // Return the updated response without modifying package details
//	    return packageRequestDto;
//	}

	@Transactional
	public PackageRequestDto updatePackageDetails(PackageRequestDto packageRequestDto) {
		String packageId = packageRequestDto.getPackageId();

		// Fetch existing package header
		DCPackageHeader existingHeader = packageHeaderRepository.findById(packageId)
				.orElseThrow(() -> new ResourceNotFoundException("Package ID not found: " + packageId));

		// Move existing header to history
		DCPackageHeaderHistory headerHistory = new DCPackageHeaderHistory();
		BeanUtils.copyProperties(existingHeader, headerHistory);
		dcPackageHeaderHistoryRepository.save(headerHistory);

		// Move matching package details to history
		List<DCPackageDetails> existingDetails = packageDetailsRepository.findByPackageId(packageId);
		for (DCPackageDetails existingDetail : existingDetails) {
			DCPackageDetailsHistory detailsHistory = new DCPackageDetailsHistory();
			BeanUtils.copyProperties(existingDetail, detailsHistory);
			dcPackageDetailsHistoryRepository.save(detailsHistory);
		}

		// Initialize total amount
		double totalAmount = 0.0;

		// Iterate over selected services and update only matching package details
		for (ServiceDto service : packageRequestDto.getSelectedServices()) {
			String packageIdLineId = service.getPackageIdLineId(); // Get packageIdLineId from DTO

			// Ensure packageId in packageIdLineId matches the given packageId before
			// updating
			if (!packageIdLineId.startsWith(packageId + "_")) {
				throw new IllegalArgumentException("Invalid packageIdLineId: " + packageIdLineId);
			}

			// Find the package details record by packageId and packageIdLineId
			Optional<DCPackageDetails> optionalDetail = packageDetailsRepository
					.findByPackageIdAndPackageIdLineId(packageId, packageIdLineId);

			if (optionalDetail.isEmpty()) {
	            throw new IllegalArgumentException("PackageIdLineId " + packageIdLineId + " not found in details.");
	        }
				DCPackageDetails packageDetail = optionalDetail.get();

				// Calculate new amount with discount
				double discountedAmount = service.getAmount() - (service.getAmount() * service.getDiscount() / 100);

				// Update package details
				packageDetail.setAmount(service.getAmount());
				packageDetail.setDiscount(service.getDiscount());
				packageDetail.setUpdatedBy(packageRequestDto.getUpdatedBy());
				packageDetail.setUpdatedDate(LocalDateTime.now());
				packageDetailsRepository.save(packageDetail);

				// Add to totalAmount
				totalAmount += discountedAmount;
			}

		// Update package header with new totalAmount
		existingHeader.setTotalAmount(totalAmount);
		existingHeader.setUpdatedBy(packageRequestDto.getUpdatedBy());
		existingHeader.setUpdatedDate(LocalDateTime.now());
		packageHeaderRepository.save(existingHeader);

		// Set updated totalAmount in response DTO
		packageRequestDto.setTotalAmount(totalAmount);

		// Return updated response
		return packageRequestDto;
	}

//	@Transactional
//	public ResponseDto updatePackageDetails(String packageId, List<PackageRequestDto> packageRequestList) {
//	    // Fetch existing package header
//	    DCPackageHeader existingHeader = packageHeaderRepository.findById(packageId)
//	            .orElseThrow(() -> new ResourceNotFoundException("Package ID not found: " + packageId));
//
//	    // Step 1: Move existing header to history
//	    DCPackageHeaderHistory headerHistory = new DCPackageHeaderHistory();
//	    headerHistory.setPackageId(existingHeader.getPackageId());
//	    headerHistory.setTotalAmount(existingHeader.getTotalAmount());
//	    headerHistory.setUpdatedBy(existingHeader.getUpdatedBy());
//	    headerHistory.setUpdatedDate(existingHeader.getUpdatedDate());
//	    dcPackageHeaderHistoryRepository.save(headerHistory);
//
//	    // Step 2: Move existing details to history
//	    List<DCPackageDetails> existingDetails = packageDetailsRepository.findByPackageId(packageId);
//	    for (DCPackageDetails existingDetail : existingDetails) {
//	        DCPackageDetailsHistory detailsHistory = new DCPackageDetailsHistory();
//	        detailsHistory.setPackageIdLineId(existingDetail.getPackageIdLineId());
//	        detailsHistory.setPackageId(existingDetail.getPackageId());
//	        detailsHistory.setUserIdStoreId(existingDetail.getUserIdStoreId());
//	        detailsHistory.setPackageName(existingDetail.getPackageName());
//	        detailsHistory.setServiceId(existingDetail.getServiceId());
//	        detailsHistory.setAmount(existingDetail.getAmount());
//	        detailsHistory.setUpdatedBy(existingDetail.getUpdatedBy());
//	        detailsHistory.setUpdatedDate(existingDetail.getUpdatedDate());
//	        detailsHistory.setDiscount(existingDetail.getDiscount());
//	        dcPackageDetailsHistoryRepository.save(detailsHistory);
//	    }
//
//	    // Step 3: Delete existing details from the main table
//	    packageDetailsRepository.deleteAll(existingDetails);
//
//	    // Step 4: Update new details
//	    double totalAmount = 0;
//	    int lineNumber = 1;
//	    List<String> packageIdLineIdList = new ArrayList<>();
//	    
//	    for (PackageRequestDto packageRequest : packageRequestList) {
//	        totalAmount += calculateTotalAmount(packageRequest);
//	        DCPackageDetails packageDetails = new DCPackageDetails();
//	        String packageIdLineId = packageId + "_" + String.format("%03d", lineNumber++);
//	        packageDetails.setPackageIdLineId(packageIdLineId);
//	        packageIdLineIdList.add(packageIdLineId);
//
//	        StoreEntity userIdStoreId = storeRepository.findByUserIdStoreId(packageRequest.getUserIdStoreId())
//	                .orElseThrow(() -> new ResourceNotFoundException("UserIdStoreId not found: " + packageRequest.getUserIdStoreId()));
//
//	        packageDetails.setUserIdStoreId(userIdStoreId.getUserIdStoreId());
//	        packageDetails.setPackageId(packageId);
//	        packageDetails.setPackageName(packageRequest.getPackageName());
//	        packageDetails.setServiceId(packageRequest.getServiceId());
//	        packageDetails.setAmount(packageRequest.getAmount());
//	        packageDetails.setUpdatedBy(packageRequest.getUpdatedBy());
//	        packageDetails.setUpdatedDate(LocalDateTime.now());
//	        packageDetails.setDiscount(packageRequest.getDiscount());
//	        packageDetailsRepository.save(packageDetails);
//	    }
//
//	    // Step 5: Update package header
//	    existingHeader.setTotalAmount(totalAmount);
//	    existingHeader.setUpdatedBy(packageRequestList.get(0).getUpdatedBy());
//	    existingHeader.setUpdatedDate(LocalDateTime.now());
//	    packageHeaderRepository.save(existingHeader);
//
//	    return new ResponseDto("success", "Package updated successfully", packageIdLineIdList);
//	}

	public List<DCPackageDetails> getDCPackageDetails(String packageIdLineId, String packageId, String amount,
			String discount, String serviceId, String serviceName, String updatedBy,
			Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("packageIdLineId", "packageId", "amount", "discount", "serviceId",
				"serviceName", "updatedBy");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DCPackageDetails> query = cb.createQuery(DCPackageDetails.class);
		Root<DCPackageDetails> root = query.from(DCPackageDetails.class);
		List<Predicate> predicates = new ArrayList<>();

		if (packageIdLineId != null) {
			predicates.add(cb.equal(root.get("packageIdLineId"), packageIdLineId));
		}
		if (packageId != null) {
			predicates.add(cb.equal(root.get("packageId"), packageId));
		}
		if (amount != null) {
			predicates.add(cb.equal(root.get("amount"), amount));
		}
		if (discount != null) {
			predicates.add(cb.equal(root.get("discount"), discount));
		}

		if (serviceId != null) {
			predicates.add(cb.equal(root.get("serviceId"), serviceId));
		}
		if (serviceName != null) {
			predicates.add(cb.equal(root.get("serviceName"), serviceName));
		}

		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	public List<DCPackageDetailsHistory> getDCPackageDetailsHistory(String packageIdLineId, String packageId,
			String amount, String discount, String serviceId, String serviceName, String updatedBy,
			Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("packageIdLineId", "packageId", "amount", "discount", "serviceId",
				"serviceName", "updatedBy");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DCPackageDetailsHistory> query = cb.createQuery(DCPackageDetailsHistory.class);
		Root<DCPackageDetailsHistory> root = query.from(DCPackageDetailsHistory.class);
		List<Predicate> predicates = new ArrayList<>();

		if (packageIdLineId != null) {
			predicates.add(cb.equal(root.get("packageIdLineId"), packageIdLineId));
		}
		if (packageId != null) {
			predicates.add(cb.equal(root.get("packageId"), packageId));
		}
		if (amount != null) {
			predicates.add(cb.equal(root.get("amount"), amount));
		}
		if (discount != null) {
			predicates.add(cb.equal(root.get("discount"), discount));
		}

		if (serviceId != null) {
			predicates.add(cb.equal(root.get("serviceId"), serviceId));
		}
		if (serviceName != null) {
			predicates.add(cb.equal(root.get("serviceName"), serviceName));
		}

		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	public StoreWithDiagnosticServiceResponseDto getLocationStoreWithServices(String location, String type, String serviceCategory) {
	    if (location == null || location.isEmpty()) {
	        throw new IllegalArgumentException("Location is a mandatory parameter.");
	    }
	    if (type == null || type.isEmpty()) {
	        throw new IllegalArgumentException("Store category is a mandatory parameter.");
	    }

	    List<StoreEntity> stores = storeRepository.findByLocationAndType(location, type);

	    if (stores.isEmpty()) {
	        throw new ResourceNotFoundException("No stores found for location: " + location + " and category: " + type);
	    }

	    List<String> storeIds = stores.stream().map(StoreEntity::getId).collect(Collectors.toList());

	    // Get services based on storeIds and serviceCategory
	    List<DiagnosticServicesEntity> services = (serviceCategory == null || serviceCategory.isEmpty())
	            ? diagnosticServiceRepository.findAllByStoreIdIn(storeIds)
	            : diagnosticServiceRepository.findByStoreIdInAndServiceCategory(storeIds, serviceCategory);

	    // Filter out stores that do not have any matching services
	    Set<String> storeIdsWithServices = services.stream()
	            .map(DiagnosticServicesEntity::getStoreId)
	            .collect(Collectors.toSet());

	    List<StoreEntity> filteredStores = stores.stream()
	            .filter(store -> storeIdsWithServices.contains(store.getId()))
	            .collect(Collectors.toList());

	    if (filteredStores.isEmpty()) {
	        throw new ResourceNotFoundException("No stores found with the specified service category: " + serviceCategory);
	    }

	    StoreWithDiagnosticServiceResponseDto responseDto = new StoreWithDiagnosticServiceResponseDto();
	    responseDto.setStores(filteredStores.stream().map(StoreDto::new).collect(Collectors.toList()));
	    responseDto.setDiagnosticServices(services.stream().map(DiagnosticServiceDto::new).collect(Collectors.toList()));

	    return responseDto;
	}

	
	//home-search-primarycare and store
	public StoreWithPrimaryCareResponseDto getLocationStoreWithPrimary(String location, String type, String serviceCategory) {
	    if (location == null || location.isEmpty()) {
	        throw new IllegalArgumentException("Location is a mandatory parameter.");
	    }
	    if (type == null || type.isEmpty()) {
	        throw new IllegalArgumentException("Store category is a mandatory parameter.");
	    }

	    List<StoreEntity> stores = storeRepository.findByLocationAndType(location, type);
	    
	    if (stores == null || stores.isEmpty()) {
	        throw new ResourceNotFoundException("No stores found for location: " + location + " and category: " + type);
	    }

	    List<String> storeIds = stores.stream()
	                                  .map(StoreEntity::getId)
	                                  .collect(Collectors.toList());

	    // Fetch services based on storeIds and serviceCategory
	    List<PrimaryCareEntity> services = new ArrayList<>();
	    if (!storeIds.isEmpty()) {
	        services = (serviceCategory == null || serviceCategory.isEmpty()) 
	                 ? primaryCareCenterRepository.findAllByStoreIdIn(storeIds)
	                 : primaryCareCenterRepository.findByStoreIdInAndServiceCategory(storeIds, serviceCategory);
	    }

	    // Filter out stores that do not have any matching services
	    Set<String> storeIdsWithServices = services.stream()
	                                               .map(PrimaryCareEntity::getStoreId)
	                                               .collect(Collectors.toSet());

	    List<StoreEntity> filteredStores = stores.stream()
	                                             .filter(store -> storeIdsWithServices.contains(store.getId()))
	                                             .collect(Collectors.toList());

	    if (filteredStores.isEmpty()) {
	        throw new ResourceNotFoundException("No stores found with the specified service category: " + serviceCategory);
	    }

	    // Create Response DTO
	    StoreWithPrimaryCareResponseDto responseDto = new StoreWithPrimaryCareResponseDto();
	    responseDto.setStores(filteredStores.stream().map(StoreDto::new).collect(Collectors.toList()));
	    responseDto.setPrimaryCare(services.stream().map(PrimaryCareDto::new).collect(Collectors.toList()));

	    return responseDto;
	}


	public List<PackageRequestDto> getPackageDetails(Optional<String> packageId, Optional<String> packageName,
			Optional<String> storeId, Optional<String> userId) {
		List<DCPackageHeader> packageHeaders;

// Build the query based on the parameters
		if (packageId.isPresent() && packageName.isPresent() && storeId.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndPackageNameAndStoreIdAndUserId(packageId.get(),
					packageName.get(), storeId.get(), userId.get());
		} else if (packageId.isPresent() && packageName.isPresent() && storeId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndPackageNameAndStoreId(packageId.get(),
					packageName.get(), storeId.get());
		} else if (packageId.isPresent() && packageName.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndPackageNameAndUserId(packageId.get(),
					packageName.get(), userId.get());
		} else if (packageId.isPresent() && storeId.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndStoreIdAndUserId(packageId.get(), storeId.get(),
					userId.get());
		} else if (packageName.isPresent() && storeId.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageNameAndStoreIdAndUserId(packageName.get(),
					storeId.get(), userId.get());
		} else if (packageId.isPresent() && packageName.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndPackageName(packageId.get(), packageName.get());
		} else if (packageId.isPresent() && storeId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndStoreId(packageId.get(), storeId.get());
		} else if (packageId.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageIdAndUserId(packageId.get(), userId.get());
		} else if (packageName.isPresent() && storeId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageNameAndStoreId(packageName.get(), storeId.get());
		} else if (packageName.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageNameAndUserId(packageName.get(), userId.get());
		} else if (storeId.isPresent() && userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByStoreIdAndUserId(storeId.get(), userId.get());
		} else if (packageId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageId(packageId.get());
		} else if (packageName.isPresent()) {
			packageHeaders = packageHeaderRepository.findByPackageName(packageName.get());
		} else if (storeId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByStoreId(storeId.get());
		} else if (userId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByUserId(userId.get());
		} else {
			packageHeaders = packageHeaderRepository.findAll();
		}

		if (packageHeaders.isEmpty()) {
			throw new ResourceNotFoundException("Package not found for the given parameters.");
		}
		List<DCPackageDetails> packageDetails = packageDetailsRepository.findAll(); 
																					
		return packageHeaders.stream().map(packageHeader -> {
			List<ServiceDto> selectedServices = packageDetails.stream()
					.filter(detail -> detail.getPackageId().equals(packageHeader.getPackageId())).map(detail -> {
						ServiceDto serviceDto = new ServiceDto();
						serviceDto.setServiceId(detail.getServiceId());
						serviceDto.setAmount(detail.getAmount());
						serviceDto.setDiscount(detail.getDiscount());
						serviceDto.setServiceName(detail.getServiceName());
						serviceDto.setUpdatedBy(detail.getUpdatedBy());
						serviceDto.setPackageIdLineId(detail.getPackageIdLineId());
						return serviceDto;
					}).collect(Collectors.toList());

			return new PackageRequestDto(packageHeader.getPackageId(), packageHeader.getPackageName(),
					packageHeader.getTotalAmount(), packageHeader.getUpdatedBy(), packageHeader.getStoreId(),
					packageHeader.getUserId(), selectedServices);
		}).collect(Collectors.toList());
	}
	
	public List<PackageRequestDto1> getHomeSearchPackageDetails(Optional<String> userIdStoreId) {
		List<DCPackageHeader> packageHeaders;

		if (userIdStoreId.isPresent()) {
			packageHeaders = packageHeaderRepository.findByUserIdStoreId(userIdStoreId.get());
		} else {
			packageHeaders = packageHeaderRepository.findAll();
		}

		if (packageHeaders.isEmpty()) {
			throw new ResourceNotFoundException("Package not found for the given parameters.");
		}
		List<DCPackageDetails> packageDetails = packageDetailsRepository.findAll(); 
																					
		return packageHeaders.stream().map(packageHeader -> {
			List<ServiceDto1> selectedServices = packageDetails.stream()
					.filter(detail -> detail.getPackageId().equals(packageHeader.getPackageId())).map(detail -> {
						ServiceDto1 serviceDto = new ServiceDto1();
						serviceDto.setServiceId(detail.getServiceId());
						serviceDto.setAmount(detail.getAmount());
						serviceDto.setDiscount(detail.getDiscount());
						serviceDto.setServiceName(detail.getServiceName());
						serviceDto.setPackageIdLineId(detail.getPackageIdLineId());
						return serviceDto;
					}).collect(Collectors.toList());

			return new PackageRequestDto1(packageHeader.getPackageId(), packageHeader.getPackageName(),
					packageHeader.getTotalAmount(),packageHeader.getUserIdStoreId(),
					selectedServices);
		}).collect(Collectors.toList());
	}

}
