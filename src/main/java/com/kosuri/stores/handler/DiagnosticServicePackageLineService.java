package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.DiagnosticServicePackageLine;
import com.kosuri.stores.dao.DiagnosticServicePackageLineHistory;
import com.kosuri.stores.dao.DiagnosticServicePackageLineHistoryRepository;
import com.kosuri.stores.dao.DiagnosticServicePackageLineRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.DiagnosticServicePackageLineRequest;
import com.kosuri.stores.utils.RandomUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiagnosticServicePackageLineService {

	@Autowired
	private DiagnosticServicePackageLineRepository diagnosticLineRepository;

	@Autowired
	private DiagnosticServicePackageLineHistoryRepository diagnosticLineHistoryRepository;

	public List<DiagnosticServicePackageLine> getDiagnosticServicePackageLines(String storeId, String userId,
			String packageId) {
		log.info(">>getDiagnosticServicePackageLines({})");
		return diagnosticLineRepository.findAllStoreAndPackageAndUser(storeId,userId,packageId);
	}

	public void addDiagnosticServicePackageLines(DiagnosticServicePackageLineRequest req) {
		log.info(">>addDiagnosticServicePackageLines({})");
		DiagnosticServicePackageLine line = new DiagnosticServicePackageLine();
		line = mapToEntity(req, line);
		diagnosticLineRepository.save(line);
	}

	public DiagnosticServicePackageLine getDiagnosticServicePackageLineById(String diagnosticLineId) {
		log.info(">>addDiagnosticServicePackageLines({})", diagnosticLineId);
		DiagnosticServicePackageLine dl = diagnosticLineRepository.findById(diagnosticLineId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Diagnostic Service Package Header Not Found By : " + diagnosticLineId));
		return dl;
	}

	public void deleteDiagnosticServicePackageLine(String diagnosticLineId) {
		DiagnosticServicePackageLine dh = diagnosticLineRepository.findById(diagnosticLineId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Diagnostic Service Package Header Not Found By : " + diagnosticLineId));
		diagnosticLineRepository.delete(dh);

	}

	public void updateDiagnosticServicePackageLine(String diagnosticHeaderId, DiagnosticServicePackageLineRequest req) {
		DiagnosticServicePackageLine dh = diagnosticLineRepository.findById(diagnosticHeaderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Diagnostic Service Package Header Not Found By : " + diagnosticHeaderId));
		dh = mapToEntity(req, dh);
		diagnosticLineRepository.save(dh);
		DiagnosticServicePackageLineHistory dhhistorry = mapToHistoryEntity(dh);
		diagnosticLineHistoryRepository.save(dhhistorry);

	}

	private DiagnosticServicePackageLine mapToEntity(DiagnosticServicePackageLineRequest req,
			DiagnosticServicePackageLine hr) {
		hr.setUpdatedBy(req.getUpdatedBy());
		hr.setUpdatedDate(LocalDateTime.now());
		hr.setAmount(req.getAmount());
		hr.setServiceId(req.getServiceId());
		hr.setDiscount(req.getDiscount());
		hr.setPackageName(req.getPackageName());
		hr.setPackageId(req.getPackageId());
		hr.setStoreId(req.getStoreId());
		hr.setServiceCategoryId(req.getServiceCategoryId());
		hr.setLineId(req.getUpdatedBy() + req.getStoreId() + req.getPackageId()
				+ RandomUtils.generateRandomThreeDigitNumber());
		return hr;
	}

	private DiagnosticServicePackageLineHistory mapToHistoryEntity(DiagnosticServicePackageLine hr) {
		DiagnosticServicePackageLineHistory history = new DiagnosticServicePackageLineHistory();
		history.setUpdatedBy(hr.getUpdatedBy());
		history.setUpdatedDate(hr.getUpdatedDate());
		history.setAmount(hr.getAmount());
		history.setPackageName(hr.getPackageName());
		history.setPackageId(hr.getPackageId());
		history.setStoreId(hr.getStoreId());
		history.setServiceCategoryId(hr.getServiceCategoryId());
		history.setUseridStoreidServiceid(hr.getLineId());
		return history;
	}

}
