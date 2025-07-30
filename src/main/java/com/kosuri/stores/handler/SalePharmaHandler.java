package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.SalePharmaRequest;
import com.kosuri.stores.model.request.StockUpdateRequest;
import com.kosuri.stores.model.response.SalePharmaResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SalePharmaHandler {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private StockHandler stockHandler;

	@Transactional
	public List<SalePharmaResponse> getSalePharmaDetails() {
		log.info(">>Service Logger getSalePharmaDetails({})");
		List<SaleEntity> sales = saleRepository.findAll();
		return sales.stream().map(pd -> {
			return modelMapper.map(pd, SalePharmaResponse.class);
		}).toList();
	}

	@Transactional
	public SalePharmaResponse getSalePharmaById(String pharmaPurchaseId) {
		log.info(">>Service Logger getSalePharmaById({})", pharmaPurchaseId);
		SaleEntity saleEntity = saleRepository.findByDocNumberLineId(pharmaPurchaseId)
				.orElseThrow(() -> new RuntimeException("Sale Pharma Detail Found By Id : " + pharmaPurchaseId));
		return modelMapper.map(saleEntity, SalePharmaResponse.class);
	}

	@Transactional
	public Map<String, Object> saveSalePharmaDetail(SalePharmaRequest salePharmaRequest, String emailId) {
		log.info(">>Service Logger saveSalePharmaDetail({})", salePharmaRequest);
		SaleEntity sale = mapToEntity(salePharmaRequest);
		updateToStockEntity(sale, emailId);
		try {

			saleRepository.save(sale);
		} catch (Exception e) {
			throw new RuntimeException("Error : " + e.getMessage());
		}
		return Map.of("response", "Pharma Purchase Detail created successfully!");

	}

	private SaleEntity mapToEntity(SalePharmaRequest request) {
		SaleEntity sale = new SaleEntity();
		sale.setCustName(request.getCustName());
		sale.setCustCode(request.getCustCode());
		sale.setPatientName(request.getPatientName());
		sale.setTime(new Date());
		sale.setDocNumber(request.getDoc_Number());
		sale.setReadableDocNo(request.getReadableDocNo());
		sale.setDate(request.getDate());
		sale.setItemCode(request.getItemCode());
		sale.setItemName(request.getItemName());
		sale.setBatchNo(request.getBatchNo());
		sale.setExpiryDate(request.getExpiryDate());
		sale.setCatCode(request.getCatCode());
		sale.setCatName(request.getCatName());
		sale.setMfacCode(request.getMfacCode());
		sale.setMfacName(request.getMfacName());
		sale.setBrandName(request.getBrandName());
		sale.setPacking(request.getPacking());
		sale.setQty(request.getQty());
		sale.setSchDisc(request.getSchDisc());
		sale.setSaleRate(request.getSaleRate());
		sale.setPurRate(request.getPurRate());
		sale.setSuppCode(request.getSuppCode());
		sale.setSuppName(request.getSuppName());
		sale.setmRP(request.getMrp());
		sale.setcGSTAmt(request.getCgstAmt());
		sale.setcGSTPer(request.getCgstPer());
		sale.setsGSTAmt(request.getSgstAmt());
		sale.setsGSTPer(request.getSgstPer());
		sale.setiGSTPer(request.getIgstPer());
		sale.setiGSTAmt(request.getIgstAmt());
		sale.setQty(request.getQty());
		sale.setSaleValue(request.getSaleValue());
		sale.setDiscValue(request.getDiscValue());
		sale.setAddCessAmt(request.getAddCessAmt());
		sale.setRoundOff(request.getRoundOff());
		sale.setSuppBillNo(request.getSuppBillNo());
		sale.setProfessional(request.getProfessional());
		sale.setMobile(request.getMobile());
		sale.setLcCode(request.getLcCode());
		sale.setPurRateWithGsT(request.getPurRateWithGsT());
		sale.setQtyBox(request.getQtyBox());
		// Discount Value Calculation || purchase value * discount percent
		double discValue = (request.getPurRate() * request.getDiscPerct()) / 100;
		sale.setDiscValue(discValue);

		// taxable Amount Calculation || purchase value * disc
		double taxableAmount = request.getPurRate() - discValue;
		sale.setTaxableAmt(taxableAmount);
		sale.setTotal(request.getTotal());
		sale.setcGSTAmt(request.getCgstAmt());

		sale.setCessAmt(request.getCessAmt());
		sale.setCessPer(request.getCessPer());
		sale.setStoreId(request.getStoreId());
		sale.setSaleRate(request.getSaleRate());
		sale.setAddCessPer(request.getAddCessPer());
		sale.setDiscPerct(request.getDiscPerct());
		return sale;
	}

	private void updateToStockEntity(SaleEntity sale, String emailId) {
		StockUpdateRequest stockUpdateRequest = new StockUpdateRequest();
		stockUpdateRequest.setTotalPurchaseValueAfterGST(sale.getPurRateWithGsT());
		stockUpdateRequest.setExpiryDate(sale.getExpiryDate());
		stockUpdateRequest.setTotalPurchaseValueAfterGST(sale.getTotal());
		stockUpdateRequest.setBatch(sale.getBatchNo());
		stockUpdateRequest.setStockUpdateRequestType(StockUpdateRequestType.PURCHASE);
		stockUpdateRequest.setQtyPerBox(sale.getQty());
		stockUpdateRequest.setPackQuantity(sale.getQty());
		stockUpdateRequest.setBalLooseQuantity(sale.getQty());
		stockUpdateRequest.setItemCode(String.valueOf(sale.getItemCode()));
		stockUpdateRequest.setItemName(sale.getItemName());
		stockUpdateRequest.setMfName(sale.getMfacName());
		stockUpdateRequest.setManufacturer(sale.getMfacCode());
		stockUpdateRequest.setStoreId(sale.getStoreId());
		stockUpdateRequest.setMrpPack(sale.getmRP() * sale.getQty());
		stockUpdateRequest.setSupplierName(sale.getSuppName());
		stockUpdateRequest.setUpdatedBy(emailId);
		try {
			stockHandler.updateStock(stockUpdateRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
