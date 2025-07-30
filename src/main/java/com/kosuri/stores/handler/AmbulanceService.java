package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosuri.stores.dao.AddAmbulance;
import com.kosuri.stores.dao.AdminStoreMembershipEntity;
import com.kosuri.stores.dao.AdminStoreMembershipRepository;
import com.kosuri.stores.dao.AmbulancePlanDetails;
import com.kosuri.stores.dao.AmbulancePlanDetailsRepo;
import com.kosuri.stores.dao.AmbulanceRepo;
import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.AmbulanceModel;
import com.kosuri.stores.model.dto.ResponseModel;
import com.kosuri.stores.model.enums.Status;
@Service
public class AmbulanceService {

	@Autowired
	AmbulanceRepo ambulanceRepo;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AdminStoreMembershipRepository adminStoreMembershipRepository;

	@Autowired
	private AmbulancePlanDetailsRepo ambulancePlanDetailsRepo;

	public ResponseModel addAmbulance(AmbulanceModel request) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}
		ResponseModel response = new ResponseModel();
		try {

			AddAmbulance ambulancExit = ambulanceRepo.findByAmbulanceRegNo(request.getAmbulanceRegNo());
			if (ambulancExit != null) {
				response.setError("true");
				response.setMsg(request.getAmbulanceRegNo() + " already register");
				return response;
			}
			request.setAmbulanceAddedDate(LocalDate.now());
			AddAmbulance ambulanEntity = objectMapper.convertValue(request, AddAmbulance.class);
			ambulanEntity.setAmbulanceOwnerId(login.get().getCId());
			String random = String.valueOf(((int) (Math.random() * (1000000 - 100000))) + 100000);
			ambulanEntity.setAmbulanceGenId("ZO" + ambulanEntity.getAmbulanceRegNo() + random);

			ambulanEntity.setStatus(Status.INACTIVE);
			ambulanceRepo.save(ambulanEntity);
			response.setError("false");
			response.setMsg("Added Successfully");
			response.setAmbulance(ambulanEntity);
//			
			String mailMessage = "Thank you for  ambulance no is " + ambulanEntity.getAmbulanceRegNo()
					+ "added successfully";
			String subject = "Add Ambulance";
			emailService.sendEmailMessage(login.get().getEmail(), mailMessage, subject);
		} catch (Exception ex) {
			response.setError("true");
			response.setMsg("Something went wrong");
			ex.printStackTrace();
		}

		return response;
	}

	public ResponseEntity<ResponseModel> updateAmbulanceDetail(AmbulanceModel request) {
		if (request.getAmbulanceGenId() == null) {
			return new ResponseEntity<ResponseModel>(new ResponseModel("Ambulance id can't be null", "true"),
					HttpStatus.BAD_REQUEST);
		}

		AddAmbulance ambulance = ambulanceRepo.findByAmbulanceGenIdAndAmbulanceOwnerId(request.getAmbulanceGenId(),
				request.getAmbulanceOwnerId());
		if (ambulance == null) {
			return new ResponseEntity<ResponseModel>(
					new ResponseModel("You don't have access to view this cab detial", "true"), HttpStatus.BAD_REQUEST);
		}

		if (ambulance.getStatus().equals("Verified")) {
			return new ResponseEntity<ResponseModel>(
					new ResponseModel("Sorry you ambulance already verified now you can't update any detail", "true"),
					HttpStatus.BAD_REQUEST);
		}

		if (request.getMobileNo() != null) {
			ambulance.setMobileNo(request.getMobileNo());
		}
		if (request.getAddress() != null) {
			ambulance.setAddress(request.getAddress());
		}
		if (request.getCity() != null) {
			ambulance.setCity(request.getCity());
		}
		if (request.getState() != null) {
			ambulance.setState(request.getState());
		}
		if (request.getDistrict() != null) {
			ambulance.setDistrict(request.getDistrict());
		}
		if (request.getPincode() != null) {
			ambulance.setPincode(request.getPincode());
		}
		if (request.getAmbulanceBrand() != null) {
			ambulance.setAmbulanceBrand(request.getAmbulanceBrand());
		}
		if (request.getAmbulanceModel() != null) {
			ambulance.setAmbulanceModel(request.getAmbulanceModel());
		}
		if (request.getModelYear() != null) {
			ambulance.setModelYear(request.getModelYear());
		}
		if (request.getCurrentMileage() != null) {
			ambulance.setCurrentMileage(request.getCurrentMileage());
		}
		if (request.getFuelType() != null) {
			ambulance.setFuelType(request.getFuelType());
		}
		if (request.getBodyType() != null) {
			ambulance.setBodyType(request.getBodyType());
		}
		if (request.getTransmission() != null) {
			ambulance.setTransmission(request.getTransmission());
		}
		if (request.getKmDriven() != null) {
			ambulance.setKmDriven(request.getKmDriven());
		}
		if (request.getNumberOfPassenger() != null) {
			ambulance.setNumberOfPassenger(request.getNumberOfPassenger());
		}
		if (request.getColor() != null) {
			ambulance.setColor(request.getColor());
		}
		if (request.getInsuranceCompanyName() != null) {
			ambulance.setInsuranceCompanyName(request.getInsuranceCompanyName());
		}
		if (request.getCertifiedCompanyName() != null) {
			ambulance.setCertifiedCompanyName(request.getCertifiedCompanyName());
		}
		if (request.getRegisteredYear() != null) {
			ambulance.setRegisteredYear(request.getRegisteredYear());
		}
		if (request.getRegisteredCity() != null) {
			ambulance.setRegisteredCity(request.getRegisteredCity());
		}
		if (request.getRegisteredState() != null) {
			ambulance.setRegisteredState(request.getRegisteredState());
		}
		if (request.getAmbulanceExpiryDate() != null) {
			ambulance.setAmbulanceExpiryDate(request.getAmbulanceExpiryDate());
		}
		if (request.getAmbulanceValidDays() != 0) {
			ambulance.setAmbulanceValidDays(request.getAmbulanceValidDays() + "");
		}

		if (request.getAmbulanceRegNo() != null) {

			AddAmbulance cabExit = ambulanceRepo.findByAmbulanceRegNo(request.getAmbulanceRegNo());
			if (cabExit != null && !cabExit.getAmbulanceGenId().equals(ambulance.getAmbulanceGenId())) {
				return new ResponseEntity<ResponseModel>(
						new ResponseModel(request.getAmbulanceRegNo() + " already register", "true"),
						HttpStatus.BAD_REQUEST);
			}

			ambulance.setAmbulanceRegNo(request.getAmbulanceRegNo());
		}

		// Check if membershipPlanId is provided
		if (request.getMemberShipPlanId() != null) {
			AdminStoreMembershipEntity membershipPlan = adminStoreMembershipRepository
					.findById(request.getMemberShipPlanId()).orElse(null);
			if (membershipPlan == null) {
				return new ResponseEntity<ResponseModel>(new ResponseModel("Invalid membership plan ID", "true"),
						HttpStatus.BAD_REQUEST);
			}

			// Update expiryDate and planType in CabPlanDetails
			AmbulancePlanDetails ambulancePlanDetails = ambulancePlanDetailsRepo.findByAmbulanceGenId(request.getAmbulanceGenId());
			if (ambulancePlanDetails == null) {
				ambulancePlanDetails = new AmbulancePlanDetails();
				ambulancePlanDetails.setAmbulanceGenId(request.getAmbulanceGenId());
			}

			ambulancePlanDetails.setPlanType(membershipPlan.getPlanId()); // Update plan type from the membership plan
			ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
			LocalDate currentDateInIST = LocalDate.now(indiaZone);
			LocalDate expiryDate = currentDateInIST.plusDays(Integer.parseInt(membershipPlan.getNoOfDays())); // Calculate expiry date
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			ambulancePlanDetails.setAmbulanceExpiryDate(expiryDate.format(formatter)); // Set expiry date in dd-MM-yyyy format
			ambulancePlanDetails.setUpdatedDate(currentDateInIST.format(formatter)); // Set the update date

			ambulancePlanDetails.setUpdatedBy(request.getAmbulanceOwnerId()); // Set the ownerId as updatedBy
			ambulancePlanDetailsRepo.save(ambulancePlanDetails); // Save the updated CabPlanDetails
		}

		ambulanceRepo.save(ambulance);
		return new ResponseEntity<ResponseModel>(new ResponseModel("Successfully Ambulance Updated"), HttpStatus.OK);
	}
	
	public ResponseEntity<?> getAmbulances(String brand, String model, LocalDate startDate, LocalDate endDate) {
		
	    List<AddAmbulance> cabs = ambulanceRepo.findByBrandOrModelOrStartDateBetweenAndEndDateBetween(brand, model, startDate, endDate);

	    // Check if the list is empty
	    if (cabs.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }

	    // Map AddCab entities to CabModel using ObjectMapper or manually
	    List<AmbulanceModel> response =cabs.stream().map(cab -> {
	    	AmbulanceModel cb = objectMapper.convertValue(cab, AmbulanceModel.class);
			cb.setInsuranceDoc(cb.getInsuranceDoc() != null ? cb.getInsuranceDoc() : "no documents found");
			cb.setAmbulancePhoto(cb.getAmbulancePhoto() != null ? cb.getAmbulancePhoto() : "no documents found");
			cb.setRcDoc(cb.getRcDoc() != null ? cb.getRcDoc() : "no documents found");
			return cb;
		}).collect(Collectors.toList());

	    // Return the response with status OK
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}


}
