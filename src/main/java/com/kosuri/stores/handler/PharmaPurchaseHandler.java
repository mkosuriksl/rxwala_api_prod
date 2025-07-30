package com.kosuri.stores.handler;

import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseRepository;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.PharmaPurchaseRequest;
import com.kosuri.stores.model.request.StockUpdateRequest;
import com.kosuri.stores.model.response.PharmaPurchaseResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PharmaPurchaseHandler {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private StockHandler stockHandler;

	@Transactional
	public List<PharmaPurchaseResponse> getPharmaPurchases(String email) {
		log.info(">>Service Logger getPharmaPurchases({})", email);
		List<PurchaseEntity> pharmaDetails = purchaseRepository.findAll();
		return pharmaDetails.stream().map(pd -> {
			return modelMapper.map(pd, PharmaPurchaseResponse.class);
		}).toList();
	}

	@Transactional
	public PharmaPurchaseResponse getPharmaPurchaseById(String pharmaPurchaseId, String email) {
		log.info(">>Service Logger getPharmaPurchaseById({})", pharmaPurchaseId);
		PurchaseEntity purchaseEntity = purchaseRepository.findByBillNoLineId(pharmaPurchaseId)
				.orElseThrow(() -> new RuntimeException("Pharma Purchase Detail Found By Id : " + pharmaPurchaseId));
		return modelMapper.map(purchaseEntity, PharmaPurchaseResponse.class);
	}

	@Transactional
	public Map<String, Object> savePharmaPurchaseDetail(PharmaPurchaseRequest pharmaPurchaseRequest, String emailId) {
		log.info(">>Service Logger savePharmaPurchaseDetail({})", pharmaPurchaseRequest);
		PurchaseEntity purchaseEntity = mapToEntity(pharmaPurchaseRequest);
		updateToStockEntity(purchaseEntity, emailId);
		try {
			purchaseRepository.save(purchaseEntity);

		} catch (Exception e) {
			throw new RuntimeException("Error : " + e.getMessage());
		}
		return Map.of("response", "Pharma Purchase Detail created successfully!");

	}

	private void updateToStockEntity(PurchaseEntity purchase, String emailId) {
		StockUpdateRequest stockUpdateRequest = new StockUpdateRequest();
		stockUpdateRequest.setExpiryDate(purchase.getExpiryDate());
		stockUpdateRequest.setTotalPurchaseValueAfterGST(purchase.getTotal());
		stockUpdateRequest.setBatch(purchase.getBatchNo());
		stockUpdateRequest.setStockUpdateRequestType(StockUpdateRequestType.PURCHASE);
		stockUpdateRequest.setQtyPerBox(purchase.getQty());
		stockUpdateRequest.setPackQuantity(purchase.getPackQty());
		stockUpdateRequest.setBalLooseQuantity(purchase.getQty());
		stockUpdateRequest.setItemCode(String.valueOf(purchase.getItemCode()));
		stockUpdateRequest.setItemName(purchase.getItemName());
		stockUpdateRequest.setMfName(purchase.getMfacName());
		stockUpdateRequest.setManufacturer(purchase.getMfacCode());
		stockUpdateRequest.setStoreId(purchase.getStoreId());
		stockUpdateRequest.setMrpPack(purchase.getMRP() * purchase.getQty());
		stockUpdateRequest.setSupplierName(purchase.getSuppName());
		stockUpdateRequest.setUpdatedBy(emailId);
		try {
			stockHandler.updateStock(stockUpdateRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PurchaseEntity mapToEntity(PharmaPurchaseRequest request) {
		PurchaseEntity purchase = new PurchaseEntity();
		purchase.setDoc_Number(request.getDoc_Number());
		purchase.setReadableDocNo(request.getReadableDocNo());
		purchase.setDate(request.getDate());
		purchase.setBillNo(request.getBillNo());
		purchase.setBillDt(request.getBillDt());
		purchase.setItemCode(request.getItemCode());
		purchase.setItemName(request.getItemName());
		purchase.setBatchNo(request.getBatchNo());
		purchase.setExpiryDate(request.getExpiryDate());
		purchase.setCatCode(request.getCatCode());
		purchase.setCatName(request.getCatName());
		purchase.setMfacCode(request.getMfacCode());
		purchase.setMfacName(request.getMfacName());
		purchase.setBrandName(request.getBrandName());
		purchase.setPacking(request.getPacking());
		purchase.setDcPrefix(request.getDcPrefix());
		purchase.setDcYear(request.getDcYear());
		purchase.setDcSrno(request.getDcSrno());
		purchase.setQty(request.getQty());
		purchase.setPackQty(request.getPackQty());
		purchase.setLooseQty(request.getLooseQty());
		purchase.setSchLooseQty(request.getSchLooseQty());
		purchase.setSchPackQty(request.getSchPackQty());
		purchase.setSchDisc(request.getSchDisc());
		purchase.setSaleRate(request.getSaleRate());
		purchase.setPurRate(request.getPurRate());
		purchase.setPurValue(request.getPurValue());
		purchase.setDiscPer(request.getDiscPer());
		purchase.setMargin(request.getMargin());
		purchase.setSuppCode(request.getSuppCode());
		purchase.setSuppName(request.getSuppName());
		purchase.setIGSTPer(request.getIgstPer());
		purchase.setGstCode(request.getGstCode());

		// Discount Value Calculation || purchase value * discount percent
		double discValue = (request.getPurValue() * request.getDiscPer()) / 100;
		purchase.setDiscValue(discValue);

		// taxable Amount Calculation || purchase value * disc
		double taxableAmount = request.getPurValue() - discValue;
		purchase.setTaxableAmt(taxableAmount);

		// calculate cgst = gst code/2
		Integer csgst = request.getGstCode() / 2;

		purchase.setCGSTPer(csgst);

		purchase.setSGSTPer(csgst);

		// Calcuate taxableAmount = taxable amount* cgst
		double cgstAmount = taxableAmount * csgst;
		purchase.setCGSTAmt(cgstAmount);

		// Calculate || sgst amount = taxable amount* sgst
		double sgstAmount = taxableAmount * csgst;
		purchase.setSGSTAmt(sgstAmount);

		// cgst amount+sgst amount
		purchase.setIGSTAmt(cgstAmount + sgstAmount);

		// Calculate Total || taxable amount + cgst amount + sgst amount
		purchase.setTotal(taxableAmount + cgstAmount + sgstAmount);

		purchase.setPost(request.getPost());
		purchase.setItemCat(request.getItemCat());
		purchase.setCessAmt(request.getCessAmt());
		purchase.setCessPer(request.getCessPer());
		purchase.setStoreId(request.getStoreId());
		purchase.setMRP(request.getMrp());
		purchase.setSaleRate(request.getSaleRate());
		return purchase;
	}

}
