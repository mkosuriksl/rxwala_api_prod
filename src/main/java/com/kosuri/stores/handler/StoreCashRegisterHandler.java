package com.kosuri.stores.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.StoreCashRegisterEntity;
import com.kosuri.stores.dao.StoreCashRegisterRepository;
import com.kosuri.stores.model.request.StoreCashRequest;
import com.kosuri.stores.model.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StoreCashRegisterHandler {

	@Autowired
	private StoreCashRegisterRepository cashRegisterRepository;

	@Transactional
	public List<StoreCashRegisterEntity> getAllStoreCashByStoreId(String storeId) {
		log.info(">>Service Logger getAllStoreCashByStoreId({})");
		return cashRegisterRepository.findByStoreId(storeId);
	}

	@Transactional
	public StoreCashRegisterEntity getStoreCashByStoreIdAndByStoreCashId(String storeId, Long storeCashId) {
		log.info(">>Service Logger getAmbulanceBookingById({})", storeId, storeCashId);
		return cashRegisterRepository.findByStoreIdAndId(storeId, storeCashId)
				.orElseThrow(() -> new RuntimeException("Store Cash Found By Id : " + storeCashId));
	}

	@Transactional
	public GenericResponse saveStoreCash(StoreCashRequest cashRequest) {
		log.info(">>Service Logger saveStoreCash({})", cashRequest);
		GenericResponse response = new GenericResponse();
		StoreCashRegisterEntity registerEntity = mapToStoreCashEntity(cashRequest);
		cashRegisterRepository.save(registerEntity);
		response.setResponseMessage("Store Cash Register created successfully!");
		return response;
	}

	private StoreCashRegisterEntity mapToStoreCashEntity(StoreCashRequest cashRequest) {
		StoreCashRegisterEntity entity = new StoreCashRegisterEntity();
		entity.setDate(cashRequest.getDate());
		entity.setSaleAmount(cashRequest.getSaleAmount());
		entity.setReturnAmount(cashRequest.getReturnAmount());
		entity.setNetAmount(cashRequest.getSaleAmount() - cashRequest.getReturnAmount());
		entity.setOnlinePay(cashRequest.getOnlinePay());
		entity.setCashPayment(cashRequest.getCashPayment());
		entity.setCashHandoverAmount(cashRequest.getCashHandoverAmount());
		entity.setHandedOverBy(cashRequest.getHandedOverBy());
		entity.setAcceptedBy(cashRequest.getAcceptedBy());
		entity.setCashInCounter(cashRequest.getCashInCounter());
		entity.setStoreId(cashRequest.getStoreId());
		return entity;
	}

}
