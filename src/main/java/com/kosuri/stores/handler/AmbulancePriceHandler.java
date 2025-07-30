package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AmbulanceMasterRepository;
import com.kosuri.stores.dao.AmbulancePriceEntity;
import com.kosuri.stores.dao.AmbulancePriceRepository;
import com.kosuri.stores.model.request.AmbulancePriceRequest;
import com.kosuri.stores.model.response.AmbulancePriceResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AmbulancePriceHandler {

	@Autowired
	private AmbulancePriceRepository ambulancePriceRepository;

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private AmbulanceMasterRepository ambulanceMasterRepository;

	@Autowired
	private ModelMapper modelMapper;

	public List<AmbulancePriceResponse> getAmbulancePrices(Long priceId, String ambulanceRegNo, Double pricePerKm,
			String updateSrcLocation, LocalDateTime updateDate, Double waitingCharges, String driverLicNo,
			String driverName, Boolean active, LocalDateTime createdOn, String createdBy, LocalDateTime updatedOn,
			String updatedBy, String contactNumber) {
		log.info(">>Service Logger getAmbulancePrices({})");
		List<AmbulancePriceEntity> ambulancePrices = ambulancePriceRepository.searchAmbulancePrices(priceId,
				ambulanceRegNo, pricePerKm, updateSrcLocation, updateDate, waitingCharges, driverLicNo, driverName,
				active, createdOn, createdBy, updatedOn, updatedBy, contactNumber);
		return ambulancePrices.stream().map(amb -> {
			return modelMapper.map(amb, AmbulancePriceResponse.class);
		}).collect(Collectors.toList());
	}

	public AmbulancePriceResponse getAmbulancePriceById(String ambulancePriceId) {
		log.info(">>Service Logger getAmbulancePriceById({})", ambulancePriceId);
		AmbulancePriceEntity ambulancePrice = ambulancePriceRepository.findById(ambulancePriceId)
				.orElseThrow(() -> new RuntimeException("Ambulance Found By Id"));
		return modelMapper.map(ambulancePrice, AmbulancePriceResponse.class);
	}

	public boolean saveAmbulancePrice(AmbulancePriceRequest priceRequest) throws Exception {
		log.info(">>Service Logger saveAmbulancePrice({})", priceRequest);
		AmbulancePriceEntity ambulancePrice = modelMapper.map(priceRequest, AmbulancePriceEntity.class);
		ambulanceMasterRepository.findById(priceRequest.getAmbulanceRegNo())
				.orElseThrow(() -> new RuntimeException("Ambulance Not Found By AmbulanceRegNo"));
		String pricePerKmStr = String.valueOf(priceRequest.getPricePerKm());
		String formattedPricePerKm = pricePerKmStr.contains(".") ? pricePerKmStr.replace(".", "") : pricePerKmStr;
		ambulancePrice.setPriceId(priceRequest.getAmbulanceRegNo() + formattedPricePerKm);
		ambulancePrice.setCreatedOn(LocalDateTime.now());
		ambulancePrice.setUpdatedOn(LocalDateTime.now());
		ambulancePrice.setActive(false);
		boolean isAmbulance;
		try {
			isAmbulance = repositoryHandler.addAmbulancePrice(ambulancePrice);
		} catch (DataIntegrityViolationException e) {
			throw new Exception(e.getCause().getCause().getMessage());
		}
		return isAmbulance;

	}

}
