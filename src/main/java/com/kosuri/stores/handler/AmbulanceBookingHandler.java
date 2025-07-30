package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.AmbulanceBookingDetailEntity;
import com.kosuri.stores.dao.AmbulanceBookingDetailRepository;
import com.kosuri.stores.dao.AmbulanceMasterEntity;
import com.kosuri.stores.dao.AmbulanceMasterRepository;
import com.kosuri.stores.model.request.AmbulanceBookingDetailRequest;
import com.kosuri.stores.model.response.AmbulanceBookingDetailResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AmbulanceBookingHandler {

	@Autowired
	private AmbulanceBookingDetailRepository ambulanceBookingRepository;

	@Autowired
	private AmbulanceMasterRepository ambulanceMasterRepository;

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private ModelMapper modelMapper;

	@Transactional
	public List<AmbulanceBookingDetailResponse> getAmbulanceBookings(String bookingNo, LocalDateTime bookingDate,
			String patientName, String fromLocation, String toLocation, String customerContNum, String contactPerson,
			String bookedBy, String status, String remarks, Boolean active, LocalDateTime createdOn, String createdBy,
			LocalDateTime updatedOn, String updatedBy, String ambulanceRegNo) {
		log.info(">>Service Logger getAmbulanceBookings({})");
		List<AmbulanceBookingDetailEntity> ambulanceBookings = ambulanceBookingRepository.searchAllFields(bookingNo,
				bookingDate, patientName, fromLocation, toLocation, customerContNum, contactPerson, bookedBy, status,
				remarks, active, createdOn, createdBy, updatedOn, updatedBy, ambulanceRegNo);
		return ambulanceBookings.stream().map(amb -> {
			return modelMapper.map(amb, AmbulanceBookingDetailResponse.class);
		}).collect(Collectors.toList());
	}

	@Transactional
	public AmbulanceBookingDetailResponse getAmbulanceBookingById(String ambulanceBookingId) {
		log.info(">>Service Logger getAmbulanceBookingById({})", ambulanceBookingId);
		AmbulanceBookingDetailEntity ambulanceBooking = ambulanceBookingRepository.findById(ambulanceBookingId)
				.orElseThrow(() -> new RuntimeException("Ambulance Found By Id"));
		return modelMapper.map(ambulanceBooking, AmbulanceBookingDetailResponse.class);
	}

	@Transactional
	public boolean saveAmbulanceBooking(AmbulanceBookingDetailRequest Request) throws Exception {
		log.info(">>Service Logger saveAmbulanceBooking({})", Request);
		AmbulanceBookingDetailEntity ambulanceBooking = modelMapper.map(Request, AmbulanceBookingDetailEntity.class);
		ambulanceMasterRepository.findById(Request.getAmbulanceRegNo())
				.orElseThrow(() -> new RuntimeException("Ambulance Not Found By AmbulanceRegNo"));
		ambulanceBooking.setBookingNo(Request.getAmbulanceRegNo() + System.currentTimeMillis());
		ambulanceBooking.setCreatedOn(LocalDateTime.now());
		ambulanceBooking.setUpdatedOn(LocalDateTime.now());
		ambulanceBooking.setActive(false);
		boolean isAmbulance;
		try {
			isAmbulance = repositoryHandler.addAmbulanceBooking(ambulanceBooking);
		} catch (DataIntegrityViolationException e) {
			throw new Exception(e.getCause().getCause().getMessage());
		}
		return isAmbulance;

	}

}
