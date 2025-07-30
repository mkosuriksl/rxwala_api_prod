package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.DiagnosticServiceRepository;
import com.kosuri.stores.dao.DiagnosticServicesEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.request.DiagnosticCenterRequestDto;
import com.kosuri.stores.model.request.DiagnosticRequest;
import com.kosuri.stores.model.request.UpdateDiagnosticCenterData;
import com.kosuri.stores.model.request.UpdateDiagnosticCenterRequestDto;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllDiagnosticCentersResponse;

@Service
public class DiagnosticHandler {

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private DiagnosticServiceRepository diagnosticServiceRepository;

	@Autowired
	private StoreHandler storeHandler;

	public GenericResponse addDiagnosticCenter(DiagnosticCenterRequestDto requests) throws Exception {
		GenericResponse response = new GenericResponse();
		for (DiagnosticRequest request : requests.getDiagnosticRequests()) {
			DiagnosticServicesEntity diagnosticServicesEntity = getDiagnosticServicesEntityFromDiagnosticRequest(
					request, requests.getStoreId(), true);
			int addCount = 1;
			try {
				if (repositoryHandler.addDiagnosticCenter(diagnosticServicesEntity)) {
					addCount++;
				} else {
					throw new APIException("Unable To Add Diagnostic Service..Issue While Adding "
							+ request.getServiceId() + " Service");
				}
			} catch (DataIntegrityViolationException e) {
				throw new Exception(e.getCause().getCause().getMessage());
			}
//			response.setResponseMessage("Diagnostic Services Add Successfully. Number Of Services Add:: " + addCount);
			response.setResponseMessage("Diagnostic Services Add Successfully.");
		}
		return response;
	}

	private DiagnosticServicesEntity getDiagnosticServicesEntityFromDiagnosticRequest(DiagnosticRequest request,
			String storeId, boolean isDCActive) {
		DiagnosticServicesEntity diagnosticServicesEntity = new DiagnosticServicesEntity();
		diagnosticServicesEntity.setServiceName(request.getServiceName());
		diagnosticServicesEntity.setUserId(request.getUserId());
		diagnosticServicesEntity.setServiceCategory(request.getServiceCategory());
		diagnosticServicesEntity.setUpdatedBy(request.getUserId());
		diagnosticServicesEntity.setPrice(request.getPrice());
		diagnosticServicesEntity.setStoreId(storeId);
		diagnosticServicesEntity.setServiceId(request.getServiceId());
		diagnosticServicesEntity.setUserServiceId(storeId + "_" + request.getServiceId());
		diagnosticServicesEntity.setStatus(isDCActive ? "1" : "0");
		diagnosticServicesEntity.setStatusUpdatedDate(LocalDateTime.now());
		diagnosticServicesEntity.setAmountUpdatedDate((LocalDateTime.now()));
		return diagnosticServicesEntity;
	}

	public boolean updateDiagnosticCenter(UpdateDiagnosticCenterRequestDto requests) throws Exception {

		for (UpdateDiagnosticCenterData request : requests.getDiagnosticRequests()) {
			DiagnosticServicesEntity serviceEntity = repositoryHandler.findServiceById(request.getUserServiceId());

			if (serviceEntity == null) {
				throw new Exception("Diagnostic Services Not Found");
			}
			serviceEntity.setServiceName(request.getServiceName());
			serviceEntity.setServiceId(request.getServiceId());
			serviceEntity.setStatusUpdatedDate(LocalDateTime.now());
			serviceEntity.setAmountUpdatedDate((LocalDateTime.now()));
			if (request.getPrice() != null && !request.getPrice().equals(serviceEntity.getPrice())) {
				serviceEntity.setPrice(request.getPrice());
				serviceEntity.setAmountUpdatedDate(LocalDateTime.now());
			}
			// Save the updated entity
			repositoryHandler.saveDiagnosticServiceEntity(serviceEntity);
		}
		return true;
	}

	public GetAllDiagnosticCentersResponse getAllDiagnosticCenters(String storeId, String serviceId) {
		GetAllDiagnosticCentersResponse response = new GetAllDiagnosticCentersResponse();
		List<DiagnosticServicesEntity> diagnosticServices = diagnosticServiceRepository
				.findAllByStoreIdAndServiceId(storeId, serviceId);
		response.setResponseMessage("Diagnostic Centers Fetched SuccessFully");
		response.setDiagnosticCenters(diagnosticServices);
		return response;
	}

	public GetAllDiagnosticCentersResponse getDiagnosticCenterByLocationOrUserIdOrStoreId(String location,
			String serviceId, String storeId) {
		GetAllDiagnosticCentersResponse response = new GetAllDiagnosticCentersResponse();
		if (location != null) {
			List<String> storeIds = storeHandler.getStoreIdFromLocation(location);
			List<DiagnosticServicesEntity> list = diagnosticServiceRepository.findAllByStoreIdIn(storeIds);
			response.setResponseMessage("Diagnostic Centers Fetched Successfully by StoreId");
			response.setDiagnosticCenters(list);
		}
		if (serviceId != null || storeId != null) {
			List<DiagnosticServicesEntity> diagnosticCenters = diagnosticServiceRepository
					.findAllByStoreIdAndServiceId(storeId, serviceId);
			response.setResponseMessage("Diagnostic Centers Fetched Successfully by StoreId");
			response.setDiagnosticCenters(diagnosticCenters);
		}
		return response;
	}

}
