package com.kosuri.stores.handler;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AmbulanceMasterEntity;
import com.kosuri.stores.dao.AmbulanceMasterRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.model.request.AmbulanceMasterRequest;
import com.kosuri.stores.model.response.AmbulanceMasterResponse;
import com.kosuri.stores.s3.config.AmazonS3Service;
import com.kosuri.stores.utils.ConvertBase64ToMultipart;
import com.kosuri.stores.utils.RandomUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AmbulanceMasterHandler {

	@Autowired
	private AmbulanceMasterRepository ambulanceMasterRepository;

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AmazonS3Service amazonService;

	@Autowired
	private TabStoreRepository storeRepository;

	private static String folderName = "Ambulance-Master/";

	public List<AmbulanceMasterResponse> getAmbulanceMasters(String ambulanceRegNo, String userId, String phoneNumber,
			String baseLocation, String vehicleBrand, String vehicleModel, String rtoRegLocation, String state,
			String vin, String ownerName, String rtoDoc, String insuDoc, String ambLicDoc, Boolean ventilator,
			String primaryCareNurse, LocalDateTime regDate, String image, String additionalFeatures, Boolean verify,
			Boolean active, String verifiedBy, String updatedby) {
		log.info(">>Service Logger getAmbulanceMasters({})");
		List<AmbulanceMasterEntity> ambulanceMasters = ambulanceMasterRepository.searchAmbulances(ambulanceRegNo,
				userId, phoneNumber, baseLocation, vehicleBrand, vehicleModel, rtoRegLocation, state, vin, ownerName,
				rtoDoc, insuDoc, ambLicDoc, ventilator, primaryCareNurse, regDate, image, additionalFeatures, verify,
				active, verifiedBy, updatedby);
		return ambulanceMasters.stream().map(amb -> {
			return modelMapper.map(amb, AmbulanceMasterResponse.class);
		}).collect(Collectors.toList());
	}

	public AmbulanceMasterResponse getAmbulanceMasterById(String ambulanceMasterId) {
		log.info(">>Service Logger getAmbulanceMasterById({})", ambulanceMasterId);
		AmbulanceMasterEntity ambulanceMaster = ambulanceMasterRepository.findById(ambulanceMasterId)
				.orElseThrow(() -> new RuntimeException("Ambulance Not Found By Id"));
		return modelMapper.map(ambulanceMaster, AmbulanceMasterResponse.class);
	}

	public boolean saveAmbulanceMaster(String email, AmbulanceMasterRequest ambulanceMasterRequest) throws Exception {
		// log.info(">>Service Logger saveAmbulanceMaster({})", ambulanceMasterRequest);
		AmbulanceMasterEntity ambulanceMaster = modelMapper.map(ambulanceMasterRequest, AmbulanceMasterEntity.class);
		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(email).get();
		ambulanceMaster.setUserId(user.getUserId());
		// Image Upload By String Value Method
		if (ambulanceMasterRequest.getImage() != null && StringUtils.isNotBlank(ambulanceMasterRequest.getImage())) {
			String fileName = UUID.randomUUID().toString();
			File file = ConvertBase64ToMultipart.saveStringImage(ambulanceMasterRequest.getImage(), fileName);
			String appFileName = amazonService.uploadStringFile(folderName, fileName, file);
			ambulanceMaster.setImage(appFileName);
		}

		// AmbLicDoc Upload By String Value Method
		if (ambulanceMasterRequest.getAmbLicDoc() != null
				&& StringUtils.isNotBlank(ambulanceMasterRequest.getAmbLicDoc())) {
			String fileName = RandomUtils.generate10RandomDigit();
			File file = ConvertBase64ToMultipart.saveStringImage(ambulanceMasterRequest.getAmbLicDoc(), fileName);
			String appFileName = amazonService.uploadStringFile(folderName, fileName, file);
			ambulanceMaster.setAmbLicDoc(appFileName);
		}

		// InsuDoc Upload By String Value Method
		if (ambulanceMasterRequest.getInsuDoc() != null
				&& StringUtils.isNotBlank(ambulanceMasterRequest.getInsuDoc())) {
			String fileName = UUID.randomUUID().toString();
			File file = ConvertBase64ToMultipart.saveStringImage(ambulanceMasterRequest.getInsuDoc(), fileName);
			String appFileName = amazonService.uploadStringFile(folderName, fileName, file);
			ambulanceMaster.setInsuDoc(appFileName);
		}

		// RtoDoc Upload By String Value Method
		if (ambulanceMasterRequest.getRtoDoc() != null && StringUtils.isNotBlank(ambulanceMasterRequest.getRtoDoc())) {
			String fileName = UUID.randomUUID().toString();
			File file = ConvertBase64ToMultipart.saveStringImage(ambulanceMasterRequest.getRtoDoc(), fileName);
			String appFileName = amazonService.uploadStringFile(folderName, fileName, file);
			ambulanceMaster.setRtoDoc(appFileName);
		}
		boolean isAmbulanceMaster;
		try {
			isAmbulanceMaster = repositoryHandler.addAmbulanceMaster(ambulanceMaster);
		} catch (DataIntegrityViolationException e) {
			throw new Exception(e.getCause().getCause().getMessage());
		}
		return isAmbulanceMaster;

	}

}
