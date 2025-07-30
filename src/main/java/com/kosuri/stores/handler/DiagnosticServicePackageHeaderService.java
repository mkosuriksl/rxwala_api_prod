package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.DiagnosticServicePackageHeader;
import com.kosuri.stores.dao.DiagnosticServicePackageHeaderHistory;
import com.kosuri.stores.dao.DiagnosticServicePackageHeaderHistoryRepository;
import com.kosuri.stores.dao.DiagnosticServicePackageHeaderRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.DiagnosticServicePackageHeaderRequest;
import com.kosuri.stores.utils.RandomUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiagnosticServicePackageHeaderService {

	@Autowired
	private DiagnosticServicePackageHeaderRepository headerRepository;

	@Autowired
	private DiagnosticServicePackageHeaderHistoryRepository headerHistoryRepository;

	public List<DiagnosticServicePackageHeader> getDiagnosticServicePackageHeaders(String storeId, String userId,
			String packageId) {
		log.info(">>getDiagnosticServicePackageHeaders({})");
		return headerRepository.findAllStoreAndUserAndPackage(storeId,userId,packageId);
	}

	public void addDiagnosticServicePackageHeaders(DiagnosticServicePackageHeaderRequest req) {
		log.info(">>addDiagnosticServicePackageHeaders({})");
		DiagnosticServicePackageHeader header = new DiagnosticServicePackageHeader();
		header = mapToEntity(req, header);
		headerRepository.save(header);
	}

	public DiagnosticServicePackageHeader getDiagnosticServicePackageHeaderById(String diagnosticHeaderId) {
		log.info(">>addDiagnosticServicePackageHeaders({})", diagnosticHeaderId);
		DiagnosticServicePackageHeader dh = headerRepository.findById(diagnosticHeaderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Diagnostic Service Package Header Not Found By : " + diagnosticHeaderId));
		return dh;
	}

	public void deleteDiagnosticServicePackageHeader(String diagnosticHeaderId) {
		DiagnosticServicePackageHeader dh = headerRepository.findById(diagnosticHeaderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Diagnostic Service Package Header Not Found By : " + diagnosticHeaderId));
		headerRepository.delete(dh);

	}

	public void updateDiagnosticServicePackageHeader(String diagnosticHeaderId,
			DiagnosticServicePackageHeaderRequest req) {
		DiagnosticServicePackageHeader dh = headerRepository.findById(diagnosticHeaderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Diagnostic Service Package Header Not Found By : " + diagnosticHeaderId));
		dh = mapToEntity(req, dh);
		headerRepository.save(dh);
		DiagnosticServicePackageHeaderHistory dhhistorry = mapToHistoryEntity(dh);
		headerHistoryRepository.save(dhhistorry);

	}

	private DiagnosticServicePackageHeader mapToEntity(DiagnosticServicePackageHeaderRequest req,
			DiagnosticServicePackageHeader hr) {
		hr.setUseridStoreidPackageid(req.getUseridStoreidPackageid());
		hr.setUpdatedBy(req.getUpdatedBy());
		hr.setUpdatedDate(LocalDateTime.now());
		hr.setAmount(req.getAmount());
		hr.setPackageName(req.getPackageName());
		hr.setPackageId(req.getPackageId());
		hr.setStoreId(req.getStoreId());
		hr.setServiceCategoryId(req.getServiceCategoryId());
		hr.setUseridStoreidPackageid(req.getUpdatedBy() + req.getStoreId() + req.getPackageId()
				+ RandomUtils.generateRandomThreeDigitNumber());
		return hr;
	}

	private DiagnosticServicePackageHeaderHistory mapToHistoryEntity(DiagnosticServicePackageHeader hr) {
		DiagnosticServicePackageHeaderHistory history = new DiagnosticServicePackageHeaderHistory();
		history.setUpdatedBy(hr.getUpdatedBy());
		history.setUpdatedDate(hr.getUpdatedDate());
		history.setAmount(hr.getAmount());
		history.setPackageName(hr.getPackageName());
		history.setPackageId(hr.getPackageId());
		history.setStoreId(hr.getStoreId());
		history.setServiceCategoryId(hr.getServiceCategoryId());
		history.setUseridStoreidPackageid(hr.getUseridStoreidPackageid());
		return history;
	}

}
