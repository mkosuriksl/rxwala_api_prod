package com.kosuri.stores.handler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseHeaderEntity;
import com.kosuri.stores.dao.PurchaseHeaderRepository;
import com.kosuri.stores.dao.PurchaseMappingEntity;
import com.kosuri.stores.dao.PurchaseMappingRepository;
import com.kosuri.stores.dao.PurchaseRepository;
import com.kosuri.stores.dao.PurchaseUpdateRequestDto;
import com.kosuri.stores.dao.PurchaseUpdateRequestEntity;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.dao.UserstoreSpecificItemMasterEntity;
import com.kosuri.stores.dao.UserstoreSpecificItemMasterRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.GstSummaryResponseDto;
import com.kosuri.stores.model.dto.ItemDetailRequest;
import com.kosuri.stores.model.dto.PurchaseInvoiceRequest;
import com.kosuri.stores.model.dto.PurchaseInvoiceResponseDto;
import com.kosuri.stores.model.dto.PurchaseUpdateFinalResponse;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.StockUpdateRequest;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
public class PurchaseHandler {
	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private StockHandler stockHandler;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private RepositoryHandler repositoryHandler;
	
	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@Autowired
    private ModelMapper modelMapper;
	
	@Autowired
	private PurchaseHeaderRepository purchaseHeaderRepository;
	
	@Autowired
	private SaleRepository saleRepository;
	
	@Autowired
	private StockRepository stockRepository;
	
	@Autowired
	private PurchaseMappingRepository purchaseMappingRepository;
	
	@Autowired 
	private UserstoreSpecificItemMasterRepository userstoreSpecificItemMasterRepository;

	@Transactional
	public void createPurchaseEntityFromRequest(MultipartFile reapExcelDataFile, String storeId, String emailId)
			throws Exception {

		Optional<StoreEntity> store = storeRepository.findById(storeId);
		if (store.isPresent()) {
			String ownerEmail = store.get().getOwnerEmail();
			if (!ownerEmail.equals(emailId)) {
				throw new APIException("User does not has access to upload file");
			}
		} else {
			throw new APIException("Store not found for given store id");
		}
		List<PurchaseEntity> purchaseArrayList = getPurchaseEntities(reapExcelDataFile, storeId);

		for (PurchaseEntity purchaseEntity : purchaseArrayList) {
			updateStock(purchaseEntity, emailId);
		}
	}

	private List<PurchaseEntity> getPurchaseEntities(MultipartFile reapExcelDataFile, String storeId) throws Exception {
		List<PurchaseEntity> purchaseArrayList = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = worksheet.iterator();
		if (rowIterator.hasNext()) {
			rowIterator.next();
		}

		while (rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();
			if (row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK) {
				try {
					createPurchaseEntityAndSave(row, purchaseArrayList, storeId);
				} catch (APIException e) {
					String message = e.getMessage();
					throw new RuntimeException(message);
				}
			}
		}
		return purchaseArrayList;
	}

	private void createPurchaseEntityAndSave(XSSFRow row, List<PurchaseEntity> purchaseArrayList, String storeId)
			throws APIException {
		PurchaseEntity tempPurchase = new PurchaseEntity();
		if (row != null) {
			tempPurchase.setDoc_Number(
					row.getCell(0) != null ? BigInteger.valueOf((long) row.getCell(0).getNumericCellValue())
							: BigInteger.valueOf(0L));
			tempPurchase.setReadableDocNo(row.getCell(1) != null && row.getCell(1).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(1).getStringCellValue()) ? row.getCell(1).getStringCellValue()
							: "");
			tempPurchase.setDate(row.getCell(2) != null ? row.getCell(2).getDateCellValue() : new Date());
			tempPurchase.setBillNo(row.getCell(3) != null && row.getCell(3).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(3).getStringCellValue()) ? row.getCell(3).getStringCellValue()
							: "");
			tempPurchase.setBillDt(row.getCell(4) != null ? row.getCell(4).getDateCellValue() : new Date());
//			tempPurchase.setItemCode(row.getCell(5) != null ? (int) row.getCell(5).getNumericCellValue() : 0);
			tempPurchase.setItemCode(row.getCell(5) != null ? String.valueOf((int) row.getCell(5).getNumericCellValue()) : "0");
			tempPurchase.setItemName(row.getCell(6) != null && row.getCell(6).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(6).getStringCellValue()) ? row.getCell(6).getStringCellValue()
							: "");
			tempPurchase.setBatchNo(row.getCell(7) != null && row.getCell(7).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(7).getStringCellValue()) ? row.getCell(7).getStringCellValue()
							: "");
			tempPurchase.setExpiryDate(row.getCell(8) != null ? row.getCell(8).getDateCellValue() : new Date());
			tempPurchase.setCatCode(row.getCell(9) != null && row.getCell(9).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(9).getStringCellValue()) ? row.getCell(9).getStringCellValue()
							: "");
			tempPurchase.setCatName(row.getCell(10) != null && row.getCell(10).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(10).getStringCellValue())
							? row.getCell(10).getStringCellValue()
							: "");
			tempPurchase.setMfacCode(row.getCell(11) != null && row.getCell(11).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(11).getStringCellValue())
							? row.getCell(11).getStringCellValue()
							: "");
			tempPurchase.setMfacName(row.getCell(12) != null && row.getCell(12).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(12).getStringCellValue())
							? row.getCell(12).getStringCellValue()
							: "");
			tempPurchase.setBrandName(row.getCell(13) != null && row.getCell(13).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(13).getStringCellValue())
							? row.getCell(13).getStringCellValue()
							: "");
			tempPurchase.setPacking(row.getCell(14) != null && row.getCell(14).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(14).getStringCellValue())
							? row.getCell(14).getStringCellValue()
							: "");
			tempPurchase.setDcYear(row.getCell(15) != null && row.getCell(15).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(15).getStringCellValue())
							? row.getCell(15).getStringCellValue()
							: "");
			tempPurchase.setDcPrefix(row.getCell(16) != null && row.getCell(16).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(16).getStringCellValue())
							? row.getCell(16).getStringCellValue()
							: "");
			tempPurchase.setDcSrno(row.getCell(17) != null ? (int) (row.getCell(17).getNumericCellValue()) : 0);
			tempPurchase.setQty(row.getCell(18) != null ? row.getCell(18).getNumericCellValue() : 0);
			tempPurchase.setPackQty(row.getCell(19) != null ? row.getCell(19).getNumericCellValue() : 0);
			tempPurchase.setLooseQty(row.getCell(20) != null ? row.getCell(20).getNumericCellValue() : 0);
			tempPurchase.setSchPackQty(row.getCell(21) != null ? row.getCell(21).getNumericCellValue() : 0);
			tempPurchase.setSchLooseQty(row.getCell(22) != null ? row.getCell(22).getNumericCellValue() : 0);
			tempPurchase.setSchDisc(row.getCell(23) != null ? row.getCell(23).getNumericCellValue() : 0);
			tempPurchase.setSaleRate(row.getCell(24) != null ? row.getCell(24).getNumericCellValue() : 0);
			tempPurchase.setPurRate(row.getCell(25) != null ? row.getCell(25).getNumericCellValue() : 0);
			tempPurchase.setMRP(row.getCell(26) != null ? row.getCell(26).getNumericCellValue() : 0);
			tempPurchase.setPurValue(row.getCell(27) != null ? row.getCell(27).getNumericCellValue() : 0);
			tempPurchase.setDiscPer(row.getCell(28) != null ? row.getCell(28).getNumericCellValue() : 0);
			tempPurchase.setMargin(row.getCell(29) != null ? row.getCell(29).getNumericCellValue() : 0);
			tempPurchase.setSuppCode(row.getCell(30) != null && row.getCell(30).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(30).getStringCellValue())
							? row.getCell(30).getStringCellValue()
							: "");
			tempPurchase.setSuppName(row.getCell(31) != null && row.getCell(31).getStringCellValue() != null
					&& StringUtils.isNotEmpty(row.getCell(31).getStringCellValue())
							? row.getCell(31).getStringCellValue()
							: "");
			tempPurchase.setDiscValue(row.getCell(32) != null ? (row.getCell(32).getNumericCellValue()) : 0);
			tempPurchase.setTaxableAmt(row.getCell(33) != null ? row.getCell(33).getNumericCellValue() : 0);
			tempPurchase.setGstCode(row.getCell(34) != null ? (int) row.getCell(34).getNumericCellValue() : 0);
			tempPurchase.setCGSTPer(row.getCell(35) != null ? (int) row.getCell(35).getNumericCellValue() : 0);
			tempPurchase.setCGSTAmt(row.getCell(36) != null ? row.getCell(36).getNumericCellValue() : 0);
			tempPurchase.setSGSTPer(row.getCell(37) != null ? (int) row.getCell(37).getNumericCellValue() : 0);
			tempPurchase.setSGSTAmt(row.getCell(38) != null ? row.getCell(38).getNumericCellValue() : 0);
			tempPurchase.setIGSTPer(row.getCell(39) != null ? row.getCell(39).getNumericCellValue() : 0);
			tempPurchase.setIGSTAmt(row.getCell(40) != null ? row.getCell(39).getNumericCellValue() : 0);
			tempPurchase.setCessPer(row.getCell(41) != null ? (int) row.getCell(41).getNumericCellValue() : 0);
			tempPurchase.setCessAmt(row.getCell(42) != null ? row.getCell(42).getNumericCellValue() : 0);
			tempPurchase.setTotal(row.getCell(43) != null ? row.getCell(43).getNumericCellValue() : 0);
			tempPurchase.setPost(row.getCell(44) != null ? row.getCell(44).getNumericCellValue() : 0);
		}
		tempPurchase.setStoreId(storeId);
		try {
			PurchaseEntity abc = purchaseRepository.save(tempPurchase);
			System.out.println(abc.getBillNoLineId());
		} catch (Exception e) {
			throw new APIException(e.getMessage());
		}
		purchaseArrayList.add(tempPurchase);
	}

	private void updateStock(PurchaseEntity purchaseEntity, String emailId) throws Exception {
		StockUpdateRequest stockUpdateRequest = new StockUpdateRequest();
		stockUpdateRequest.setExpiryDate(purchaseEntity.getExpiryDate());
		stockUpdateRequest.setBalLooseQuantity(purchaseEntity.getLooseQty());
		stockUpdateRequest.setBatch(purchaseEntity.getBatchNo());
		stockUpdateRequest.setStockUpdateRequestType(StockUpdateRequestType.PURCHASE);
		stockUpdateRequest.setQtyPerBox(purchaseEntity.getQty());
		stockUpdateRequest.setPackQuantity(purchaseEntity.getPackQty());
		stockUpdateRequest.setBalLooseQuantity(purchaseEntity.getLooseQty());
		stockUpdateRequest.setItemCategory(purchaseEntity.getItemCat());
		stockUpdateRequest.setItemCode(purchaseEntity.getItemCode().toString());
		stockUpdateRequest.setItemName(purchaseEntity.getItemName());
		stockUpdateRequest.setMfName(purchaseEntity.getMfacName());
		stockUpdateRequest.setManufacturer(purchaseEntity.getMfacCode());
		stockUpdateRequest.setStoreId(purchaseEntity.getStoreId());
		stockUpdateRequest.setMrpPack(purchaseEntity.getMRP());
		stockUpdateRequest.setTotalPurchaseValueAfterGST(purchaseEntity.getTotal());
		stockUpdateRequest.setSupplierName(purchaseEntity.getSuppName());
		stockUpdateRequest.setUpdatedBy(emailId);

		stockHandler.updateStock(stockUpdateRequest);
	}

	public List<StockEntity> searchStockByBusinessType(String storeId, String businessType) throws APIException {
		return repositoryHandler.getStockRecordsByBusinessType(storeId, businessType);
	}

	public List<PurchaseEntity> savePurchaseInvoices(PurchaseInvoiceRequest invoiceRequest) {
		List<PurchaseEntity> purchases = maptoRequest(invoiceRequest);
		purchaseRepository.saveAll(purchases);
		return purchases;
	}

	private List<PurchaseEntity> maptoRequest(PurchaseInvoiceRequest invoiceRequest) {
		return invoiceRequest.getDetailRequests().stream().map(req -> {
			PurchaseEntity entity = new PurchaseEntity();
			entity.setStoreId(invoiceRequest.getStoreId());
			entity.setSuppCode(invoiceRequest.getSupplierCode());
			entity.setSuppName(invoiceRequest.getSupplierName());
			entity.setDate(invoiceRequest.getPurchaseDate());
			entity.setBillNo(invoiceRequest.getInvoiceNo());
			entity.setItemCat(invoiceRequest.getItemCategory());
			entity.setCatCode(invoiceRequest.getItemSubCategory());
			entity.setItemCode(req.getItemCode());
			entity.setItemName(req.getItemName());
			entity.setBatchNo(req.getBatchNo());
			entity.setExpiryDate(req.getExpiryDate());
			entity.setCatName(req.getCatName());
			entity.setMfacCode(req.getMfacCode());
			entity.setMfacName(req.getMfacName());
			entity.setBrandName(req.getBrandName());
			entity.setPacking(req.getPacking());
			entity.setQty(req.getQtyOrBox());
			entity.setPackQty(req.getPackQty());
			entity.setLooseQty(req.getLooseQty());
			entity.setSchDisc(req.getSchDisc());
			entity.setPurRate(req.getPurRate());
			entity.setMRP(req.getMrp());
			entity.setPurValue(req.getPurValue());
			entity.setDiscPer(req.getDiscPer());
			entity.setDiscValue(req.getDiscValue());
			entity.setTaxableAmt(req.getTaxableAmount());
			entity.setGstCode(req.getGstCode());
			entity.setGstCode(req.getGstCode());
			entity.setCGSTPer(req.getCgstPer());
			entity.setCGSTAmt(req.getCgstAmount());
			entity.setIGSTPer(req.getIgstPer());
			entity.setIGSTAmt(req.getIgstAmount());
			entity.setCessPer(req.getCessPer());
			entity.setCessAmt(req.getCessAmount());
			entity.setTotal(req.getTotal());
			return entity;
		}).toList();
	}
	
	public List<PurchaseEntity> savePurchaseInvoicesEn(PurchaseInvoiceRequest invoiceRequest) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		 Optional<StoreEntity> storeEntityOptional = storeRepository.findById(invoiceRequest.getStoreId());

		    // Check if the store exists and is active
		    if (storeEntityOptional.isEmpty() || !"active".equalsIgnoreCase(storeEntityOptional.get().getStatus())) {
		        throw new ResourceNotFoundException("Store is either not found or not active.");
		    }
		    double totalTaxable = 0.0;
		    double totalCgst = 0.0;
		    double totalSgst = 0.0;
		    double totalIgst = 0.0;
		    double totalCess = 0.0;
		    double grandTotal = 0.0;

		    for (ItemDetailRequest detail : invoiceRequest.getDetailRequests()) {
		        totalTaxable += detail.getTaxableAmount() != null ? detail.getTaxableAmount() : 0.0;
		        totalCgst += detail.getCgstAmount() != null ? detail.getCgstAmount() : 0.0;
		        totalSgst += detail.getSgstAmount() != null ? detail.getSgstAmount() : 0.0;
		        totalIgst += detail.getIgstAmount() != null ? detail.getIgstAmount() : 0.0;
		        totalCess += detail.getCessAmount() != null ? detail.getCessAmount() : 0.0;
		        grandTotal += detail.getTotal() != null ? detail.getTotal() : 0.0;
		    }
		    PurchaseHeaderEntity header = new PurchaseHeaderEntity();
		    header.setBillNo(invoiceRequest.getInvoiceNo());
		    header.setDate(invoiceRequest.getPurchaseDate());
		    header.setSuppCode(invoiceRequest.getSupplierCode());
		    header.setSuppName(invoiceRequest.getSupplierName());
		    header.setStoreId(invoiceRequest.getStoreId());
		    header.setUserIdStoreId(login.get().getUserId() + "_" + invoiceRequest.getStoreId());
		    purchaseHeaderRepository.save(header);
		List<PurchaseEntity> purchases = maptoRequestEn(invoiceRequest,invoiceRequest.getInvoiceNo());
		purchaseRepository.saveAll(purchases);
		return purchases;
	}

	private List<PurchaseEntity> maptoRequestEn(PurchaseInvoiceRequest invoiceRequest,String invoiceNumber) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		
		
		 AtomicInteger counter = new AtomicInteger(1); 
		return invoiceRequest.getDetailRequests().stream().map(req -> {
			PurchaseEntity entity = new PurchaseEntity();
			entity.setBillNo(invoiceNumber);
			String lineId = String.format("%s_%04d", invoiceNumber, counter.getAndIncrement());
	        entity.setBillNoLineId(lineId);
			entity.setStoreId(invoiceRequest.getStoreId());
			entity.setSuppCode(invoiceRequest.getSupplierCode());
			entity.setSuppName(invoiceRequest.getSupplierName());
			entity.setDate(invoiceRequest.getPurchaseDate());
						
			StockEntity stock=stockRepository.findByUserIdStoreIdItemCodeAndBatch(login.get().getUserId()+"_"+invoiceRequest.getStoreId()+"_"+req.getItemCode(),req.getBatchNo());
			
			if (stock != null) {
				Double newPackQty = stock.getBalPackQuantity() + (req.getPackQty() != null ? req.getPackQty() : 0.0);
			    Double newQty = newPackQty * (req.getQtyOrBox()!= null ? req.getQtyOrBox() : 0.0)+(stock.getBalLooseQuantity());
			    

			    stock.setBalPackQuantity(newPackQty);
			    stock.setBalQuantity(newQty);

			    stockRepository.save(stock);
			}
			else {
				StockEntity newStock = new StockEntity();
			    			    String userId = login.get().getUserId();
			    String storeId = invoiceRequest.getStoreId();
			    String itemCode = req.getItemCode();
			    String userStoreItemKey = userId + "_" + storeId + "_" + itemCode;
			    String userIdStoreId=userId+"_"+storeId;
			    newStock.setUserId(userId);
			    newStock.setUserIdStoreId(userIdStoreId);
			    newStock.setStoreId(storeId);
			    newStock.setItemCode(itemCode);
			    newStock.setItemName(req.getItemName());
			    newStock.setBatch(req.getBatchNo());
			    newStock.setManufacturer(req.getMfacName());
			    newStock.setUpdatedAt(LocalDateTime.now());
				newStock.setBalPackQuantity(req.getPackQty() != null ? req.getPackQty() : 0.0);
				newStock.setBalQuantity((req.getLooseQty()!=null?req.getLooseQty():0.0)+((req.getQtyOrBox()!=null?req.getQtyOrBox():0.0)*(req.getPackQty()!=null?req.getPackQty():0.0)));
			    newStock.setBalLooseQuantity(req.getLooseQty() != null ? req.getLooseQty() : 0.0);
				

			    newStock.setUserIdStoreIdItemCode(userStoreItemKey);
			    newStock.setMfName(req.getMfacName());
			    newStock.setExpiryDate(req.getExpiryDate());
			    newStock.setItemCategory(invoiceRequest.getItemCategory());
			    newStock.setMrpPack(invoiceRequest.getDetailRequests().get(0).getMrp());
			    newStock.setMrpValue(invoiceRequest.getDetailRequests().get(0).getMrp());
			    
			    stockRepository.save(newStock);
				
			}
			entity.setItemCat(invoiceRequest.getItemCategory());
			entity.setCatCode(invoiceRequest.getItemSubCategory());
			entity.setItemCode(invoiceRequest.getDetailRequests().get(0).getItemCode());
			entity.setItemName(invoiceRequest.getDetailRequests().get(0).getItemName());
			entity.setBatchNo(invoiceRequest.getDetailRequests().get(0).getBatchNo());
			entity.setExpiryDate(invoiceRequest.getDetailRequests().get(0).getExpiryDate());
			entity.setCatName(invoiceRequest.getDetailRequests().get(0).getCatName());
			entity.setMfacCode(invoiceRequest.getDetailRequests().get(0).getMfacCode());
			entity.setMfacName(invoiceRequest.getDetailRequests().get(0).getMfacName());
			entity.setBrandName(invoiceRequest.getDetailRequests().get(0).getBrandName());
			entity.setPacking(invoiceRequest.getDetailRequests().get(0).getPacking());
			entity.setQty(invoiceRequest.getDetailRequests().get(0).getQtyOrBox());
			entity.setPackQty(invoiceRequest.getDetailRequests().get(0).getPackQty());
			entity.setLooseQty(invoiceRequest.getDetailRequests().get(0).getLooseQty());
			entity.setSchDisc(invoiceRequest.getDetailRequests().get(0).getSchDisc());
			entity.setPurRate(invoiceRequest.getDetailRequests().get(0).getPurRate());
			entity.setMRP(invoiceRequest.getDetailRequests().get(0).getMrp());
			entity.setPurValue(invoiceRequest.getDetailRequests().get(0).getPurValue());
			entity.setDiscPer(invoiceRequest.getDetailRequests().get(0).getDiscPer());
			entity.setDiscValue(invoiceRequest.getDetailRequests().get(0).getDiscValue());
			entity.setTaxableAmt(invoiceRequest.getDetailRequests().get(0).getTaxableAmount());
			entity.setGstCode(invoiceRequest.getDetailRequests().get(0).getGstCode());
			entity.setGstCode(invoiceRequest.getDetailRequests().get(0).getGstCode());
			entity.setCGSTPer(invoiceRequest.getDetailRequests().get(0).getCgstPer());
			entity.setCGSTAmt(invoiceRequest.getDetailRequests().get(0).getCgstAmount());
			entity.setIGSTPer(invoiceRequest.getDetailRequests().get(0).getIgstPer());
			entity.setIGSTAmt(invoiceRequest.getDetailRequests().get(0).getIgstAmount());
			entity.setCessPer(invoiceRequest.getDetailRequests().get(0).getCessPer());
			entity.setCessAmt(invoiceRequest.getDetailRequests().get(0).getCessAmount());
			entity.setTotal(invoiceRequest.getDetailRequests().get(0).getTotal());
			entity.setDiscount(invoiceRequest.getDetailRequests().get(0).getDiscount());
			entity.setAfterDiscount(invoiceRequest.getDetailRequests().get(0).getAfterDiscount());
			entity.setTotalPurchasePrice(invoiceRequest.getDetailRequests().get(0).getTotalPurchasePrice());
			entity.setSGSTAmt(invoiceRequest.getDetailRequests().get(0).getSgstAmount());
			entity.setSGSTPer(invoiceRequest.getDetailRequests().get(0).getSgstPer());
			entity.setUserId(login.get().getUserId());
			entity.setUserIdStoreId(login.get().getUserId()+"_"+invoiceRequest.getStoreId());
			entity.setUserIdStoreIdItemCode(login.get().getUserId()+"_"+invoiceRequest.getStoreId()+"_"+req.getItemCode());
			
			// Save unique GST-item mapping if not already present
			if (!purchaseMappingRepository.existsByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode())) {
				PurchaseMappingEntity mapping = new PurchaseMappingEntity();
			    mapping.setUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());
			    mapping.setGstCode(entity.getGstCode());
			    mapping.setItemCode(entity.getItemCode());
			    mapping.setItemCat(entity.getItemCat());
			    mapping.setStoreId(entity.getStoreId());
			    mapping.setUserId(entity.getUserId());
			    purchaseMappingRepository.save(mapping);
			    
			    
			}
			// Save to UserstoreSpecificItemMasterEntity if not already exists
			if (!userstoreSpecificItemMasterRepository.existsById(entity.getUserIdStoreIdItemCode())) {
			    UserstoreSpecificItemMasterEntity itemMaster = new UserstoreSpecificItemMasterEntity();
			    itemMaster.setUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());
			    itemMaster.setUserId(entity.getUserId());
			    itemMaster.setUserIdStoreId(entity.getUserIdStoreId());
			    itemMaster.setStoreId(entity.getStoreId());
			    itemMaster.setItemCode(entity.getItemCode());
			    itemMaster.setItemName(req.getItemName());
			    itemMaster.setItemCategory(invoiceRequest.getItemCategory());
			    itemMaster.setItemSubCategory(invoiceRequest.getItemSubCategory());
			    itemMaster.setManufacturer(req.getMfacName());
			    itemMaster.setBrand(req.getBrandName());
			    itemMaster.setGst(req.getGstCode());
			    itemMaster.setUpdatedBy(entity.getUserId()); // or login.get().getUserId()
			    itemMaster.setUpdatedDate(new Date());
			    
			    userstoreSpecificItemMasterRepository.save(itemMaster);
			}


			return entity;
			
		}).toList();
	}
	
//	public List<PurchaseInvoiceResponseDto> getFilteredPurchases(String invoiceNo, String suppName, String storeId, Date fromDate, Date toDate,String userIdStoreId) {
//	    Specification<PurchaseHeaderEntity> spec = (root, query, cb) -> {
//	        List<Predicate> predicates = new ArrayList<>();
//
//	        if (invoiceNo != null) {
//	            predicates.add(cb.equal(root.get("billNo"), invoiceNo));
//	        }
//	        if (suppName != null) {
//	            predicates.add(cb.equal(root.get("suppName"), suppName));
//	        }
//	        if (storeId != null) {
//	            predicates.add(cb.equal(root.get("storeId"), storeId));
//	        }
//	        if (fromDate != null && toDate != null) {
//	            predicates.add(cb.between(root.get("date"), fromDate, toDate));
//	        } else if (fromDate != null) {
//	            predicates.add(cb.equal(root.get("date"), fromDate));
//	        }
//	        if (userIdStoreId != null) {
//	            predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
//	        }
//
//	        return cb.and(predicates.toArray(new Predicate[0]));
//	    };
//
//	    List<PurchaseHeaderEntity> purchaseEntities = purchaseHeaderRepository.findAll(spec);
//
//	    // Convert entity list to DTO list
//	    return purchaseEntities.stream()
//	            .map(p -> new PurchaseInvoiceResponseDto(
//	                    p.getBillNo(),
//	                    p.getDate(),
//	                    p.getSuppName(),
//	                    p.getStoreId(),
//	                    p.getUserIdStoreId()))
//	            .collect(Collectors.toList());
//	}
	
	public Page<PurchaseInvoiceResponseDto> getFilteredPurchases(
	        String invoiceNo,
	        String suppName,
	        String storeId,
	        Date fromDate,
	        Date toDate,
	        String userIdStoreId,
	        Pageable pageable) {

	    Specification<PurchaseHeaderEntity> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (invoiceNo != null && !invoiceNo.isEmpty()) {
	            predicates.add(cb.equal(root.get("billNo"), invoiceNo));
	        }
	        if (suppName != null && !suppName.isEmpty()) {
	            predicates.add(cb.equal(root.get("suppName"), suppName));
	        }
	        if (storeId != null && !storeId.isEmpty()) {
	            predicates.add(cb.equal(root.get("storeId"), storeId));
	        }
	        if (fromDate != null && toDate != null) {
	            predicates.add(cb.between(root.get("date"), fromDate, toDate));
	        } else if (fromDate != null) {
	            predicates.add(cb.equal(root.get("date"), fromDate));
	        }
	        if (userIdStoreId != null && !userIdStoreId.isEmpty()) {
	            predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    Page<PurchaseHeaderEntity> pageEntities = purchaseHeaderRepository.findAll(spec, pageable);

	    List<PurchaseInvoiceResponseDto> dtoList = pageEntities.getContent().stream()
	            .map(p -> new PurchaseInvoiceResponseDto(
	                    p.getBillNo(),
	                    p.getDate(),
	                    p.getSuppName(),
	                    p.getStoreId(),
	                    p.getUserIdStoreId()))
	            .collect(Collectors.toList());

	    return new PageImpl<>(dtoList, pageable, pageEntities.getTotalElements());
	}


	
//	public List<PurchaseEntity> getPurchasesInovices(String invoiceNo,String userIdStoreId) {
//	    Specification<PurchaseEntity> spec = (root, query, cb) -> {
//	        List<Predicate> predicates = new ArrayList<>();
//
//	        if (invoiceNo != null) {
//	            predicates.add(cb.equal(root.get("billNo"), invoiceNo));
//	        }
//	        if (userIdStoreId != null) {
//	            predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
//	        }
//
//	        return cb.and(predicates.toArray(new Predicate[0]));
//	    };
//
//	    	return purchaseRepository.findAll(spec);
//
//	    
//	}
	
	public Page<PurchaseEntity> getPurchasesInovices(String invoiceNo,String userIdStoreId, Pageable pageable) {
	    Specification<PurchaseEntity> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (invoiceNo != null) {
	            predicates.add(cb.equal(root.get("billNo"), invoiceNo));
	        }
	        if (userIdStoreId != null) {
	            predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    	return purchaseRepository.findAll(spec,pageable);

	    
	}
	
	public PurchaseUpdateFinalResponse updatePurchasesByInvoice(PurchaseUpdateRequestDto requestDto) {

	    List<PurchaseEntity> invoiceRecords = purchaseRepository.findByBillNo(requestDto.getInvoiceNo());

	    if (invoiceRecords.isEmpty()) {
	        PurchaseUpdateFinalResponse response = new PurchaseUpdateFinalResponse();
	        response.setInvoiceNo(requestDto.getInvoiceNo());
	        response.setStatus("error");
	        response.setMessage("Invoice number '" + requestDto.getInvoiceNo() + "' not found.");
	        response.setUpdated(Collections.emptyList());
	        return response;
	    }

	    List<PurchaseEntity> updatedList = new ArrayList<>();
	    List<String> errorMessages = new ArrayList<>();

	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
	    if (login.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
	    }

	    for (PurchaseUpdateRequestEntity updateRequest : requestDto.getUpdates()) {

	        String uniqueKey = updateRequest.getUserIdStoreIdItemCode();

	        Optional<PurchaseEntity> existing = purchaseRepository
	            .findByUserIdStoreIdItemCode(uniqueKey)
	            .filter(e -> !e.getBillNoLineId().equals(updateRequest.getBillNoLineId()));

	        if (existing.isPresent()) {
	            errorMessages.add("Duplicate entry found for userIdStoreIdItemCode: '" + uniqueKey + "'");
	            continue;
	        }

	        PurchaseEntity entity = new PurchaseEntity();
	        entity.setBillNoLineId(updateRequest.getBillNoLineId());
	        entity.setDoc_Number(updateRequest.getDocNumber());
	        entity.setReadableDocNo(updateRequest.getReadableDocNo());
	        entity.setDate(updateRequest.getDate());
	        entity.setBillNo(requestDto.getInvoiceNo());
	        entity.setBillDt(updateRequest.getBillDt());
	        entity.setItemCode(updateRequest.getItemCode());
	        entity.setItemName(updateRequest.getItemName());
	        entity.setBatchNo(updateRequest.getBatchNo());
	        entity.setExpiryDate(updateRequest.getExpiryDate());
	        entity.setCatCode(updateRequest.getCatCode());
	        entity.setCatName(updateRequest.getCatName());
	        entity.setMfacCode(updateRequest.getMfacCode());
	        entity.setMfacName(updateRequest.getMfacName());
	        entity.setBrandName(updateRequest.getBrandName());
	        entity.setPacking(updateRequest.getPacking());
	        entity.setDcYear(updateRequest.getDcYear());
	        entity.setDcPrefix(updateRequest.getDcPrefix());
	        entity.setDcSrno(updateRequest.getDcSrno());
	        entity.setQty(updateRequest.getQty());
	        entity.setPackQty(updateRequest.getPackQty());
	        entity.setLooseQty(updateRequest.getLooseQty());
	        entity.setSchPackQty(updateRequest.getSchPackQty());
	        entity.setSchLooseQty(updateRequest.getSchLooseQty());
	        entity.setSchDisc(updateRequest.getSchDisc());
	        entity.setSaleRate(updateRequest.getSaleRate());
	        entity.setPurRate(updateRequest.getPurRate());
	        entity.setMRP(updateRequest.getMRP());
	        entity.setPurValue(updateRequest.getPurValue());
	        entity.setDiscPer(updateRequest.getDiscPer());
	        entity.setMargin(updateRequest.getMargin());
	        entity.setSuppCode(updateRequest.getSuppCode());
	        entity.setSuppName(updateRequest.getSuppName());
	        entity.setDiscValue(updateRequest.getDiscValue());
	        entity.setTaxableAmt(updateRequest.getTaxableAmt());
	        entity.setGstCode(updateRequest.getGstCode());
	        entity.setCGSTPer(updateRequest.getCGSTPer());
	        entity.setSGSTPer(updateRequest.getSGSTPer());
	        entity.setCGSTAmt(updateRequest.getCGSTAmt());
	        entity.setSGSTAmt(updateRequest.getSGSTAmt());
	        entity.setIGSTPer(updateRequest.getIGSTPer());
	        entity.setIGSTAmt(updateRequest.getIGSTAmt());
	        entity.setTotal(updateRequest.getTotal());
	        entity.setPost(updateRequest.getPost());
	        entity.setItemCat(updateRequest.getItemCat());
	        entity.setCessPer(updateRequest.getCessPer());
	        entity.setCessAmt(updateRequest.getCessAmt());
	        entity.setStoreId(updateRequest.getStoreId());
	        entity.setUserId(login.get().getUserId());
	        entity.setDiscount(updateRequest.getDiscount());
	        entity.setAfterDiscount(updateRequest.getAfterDiscount());
	        entity.setTotalPurchasePrice(updateRequest.getTotalPurchasePrice());
//	        entity.setUserIdStoreId(updateRequest.getUserIdStoreId());
//	        entity.setUserIdStoreIdItemCode(uniqueKey);
//			entity.setUserIdStoreIdItemCode(login.get().getUserId() + "_" + updateRequest.getStoreId() + "_" + updateRequest.getItemCode());
//			entity.setUserIdStoreId(login.get().getUserId() + "_" + updateRequest.getStoreId());

	        try {
	            purchaseRepository.save(entity);
	            updatedList.add(modelMapper.map(entity, PurchaseEntity.class));
	        } catch (DataIntegrityViolationException e) {
	            Throwable rootCause = ExceptionUtils.getRootCause(e);
	            String errorMsg = rootCause != null ? rootCause.getMessage() : e.getMessage();
	            errorMessages.add("Duplicate entry for userIdStoreIdItemCode '" + uniqueKey + "': " + errorMsg);
	        }
	    }

	    PurchaseUpdateFinalResponse finalResponse = new PurchaseUpdateFinalResponse();
	    finalResponse.setInvoiceNo(requestDto.getInvoiceNo());
	    finalResponse.setUpdated(updatedList);

	    if (errorMessages.isEmpty()) {
	        finalResponse.setMessage(updatedList.size() + " records processed successfully.");
	        finalResponse.setStatus("success");
	    } else {
	        finalResponse.setMessage(updatedList.size() + " records updated. " + errorMessages.size() +
	                " skipped due to errors: " + String.join("; ", errorMessages));
	        finalResponse.setStatus("partial_success");
	    }

	    return finalResponse;

	}
	
	public Map<String, Object> updatePurchase(List<PurchaseEntity> entities) {
	    int successCount = 0;
	    List<String> failedKeys = new ArrayList<>();

	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
	    if (login.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
	    }
	    for (PurchaseEntity entity : entities) {
//	        Optional<PurchaseEntity> optional = purchaseRepository.findByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());
	    	Optional<PurchaseEntity> optional = purchaseRepository.findByBillNoLineId(entity.getBillNoLineId());
	        if (optional.isPresent()) {
	            PurchaseEntity existing = optional.get();

	            // Copy required fields from existing entity to the new one
	            entity.setBillNoLineId(existing.getBillNoLineId());
	            entity.setDoc_Number(entities.get(0).getDoc_Number());
	            entity.setReadableDocNo(entities.get(0).getReadableDocNo());
	            entity.setDate(entities.get(0).getDate());
	            entity.setBillNo(entities.get(0).getBillNo());
	            entity.setBillDt(entities.get(0).getBillDt());
	            
	            StockEntity stock=stockRepository.findByItemCodeAndBatch(entities.get(0).getItemCode(),entities.get(0).getBatchNo());
				
				if(stock!=null) {
					entity.setItemCode(stock.getItemCode());
					stock.setBalQuantity(stock.getBalQuantity()+entities.get(0).getQty());
					stock.setBalPackQuantity(stock.getBalPackQuantity()+entities.get(0).getPackQty());
					stock.setBalLooseQuantity(stock.getBalLooseQuantity()+entities.get(0).getLooseQty());
				    stockRepository.save(stock);
				}
	            entity.setItemName(entities.get(0).getItemName());
	            entity.setBatchNo(entities.get(0).getBatchNo());
	            entity.setExpiryDate(entities.get(0).getExpiryDate());
	            entity.setCatCode(entities.get(0).getCatCode());
	            entity.setCatName(entities.get(0).getCatName());
	            entity.setMfacCode(entities.get(0).getMfacCode());
	            entity.setMfacName(entities.get(0).getMfacName());
	            entity.setBrandName(entities.get(0).getBrandName());
	            entity.setPacking(entities.get(0).getPacking());
	            entity.setDcYear(entities.get(0).getDcYear());
	            entity.setDcPrefix(entities.get(0).getDcPrefix());
	            entity.setDcSrno(entities.get(0).getDcSrno());
	            entity.setQty(entities.get(0).getQty());
	            entity.setPackQty(entities.get(0).getPackQty());
	            entity.setLooseQty(entities.get(0).getLooseQty());
	            entity.setSchPackQty(entities.get(0).getSchPackQty());
	            entity.setSchLooseQty(entities.get(0).getSchLooseQty());
	            entity.setSchDisc(entities.get(0).getSchDisc());
	            entity.setSaleRate(entities.get(0).getSaleRate());
	            entity.setPurRate(entities.get(0).getPurRate());
	            entity.setMRP(entities.get(0).getMRP());
	            entity.setPurValue(entities.get(0).getPurValue());
	            entity.setDiscPer(entities.get(0).getDiscPer());
	            entity.setMargin(entities.get(0).getMargin());
	            entity.setSuppCode(entities.get(0).getSuppCode());
	            entity.setSuppName(entities.get(0).getSuppName());
	            entity.setDiscValue(entities.get(0).getDiscValue());
	            entity.setTaxableAmt(entities.get(0).getTaxableAmt());
	            entity.setGstCode(entities.get(0).getGstCode());
	            entity.setCGSTPer(entities.get(0).getCGSTPer());
	            entity.setSGSTPer(entities.get(0).getSGSTPer());
	            entity.setCGSTAmt(entities.get(0).getCGSTAmt());
	            entity.setSGSTAmt(entities.get(0).getSGSTAmt());
	            entity.setIGSTPer(entities.get(0).getIGSTPer());
	            entity.setIGSTAmt(entities.get(0).getIGSTAmt());
	            entity.setTotal(entities.get(0).getTotal());
	            entity.setPost(entities.get(0).getPost());
	            entity.setItemCat(entities.get(0).getItemCat());
	            entity.setCessPer(entities.get(0).getCessPer());
	            entity.setCessAmt(entities.get(0).getCessAmt());
	            entity.setStoreId(entities.get(0).getStoreId());
	            entity.setDiscount(entities.get(0).getDiscount());
	            entity.setAfterDiscount(entities.get(0).getAfterDiscount());
	            entity.setTotalPurchasePrice(entities.get(0).getTotalPurchasePrice());
	            purchaseRepository.save(entity);
	            successCount++;
	        } else {
	            failedKeys.add(entity.getUserIdStoreIdItemCode());
	        }
	    }

	    Map<String, Object> response = new HashMap<>();
	    if (failedKeys.isEmpty()) {
	        response.put("status", "success");
	        response.put("message", successCount + " records updated successfully.");
	    } else if (successCount == 0) {
	        response.put("status", "failed");
	        response.put("message", "No records updated. Missing keys: " + failedKeys);
	    } else {
	        response.put("status", "partial_success");
	        response.put("message", successCount + " records updated. Failed keys: " + failedKeys);
	    }

	    return response;
	}
	
	public List<GstSummaryResponseDto> getGstSummary(String storeId, Date fromDate, Date toDate) {
	    List<PurchaseEntity> records;

	    if (storeId != null && fromDate != null && toDate != null) {
	        records = purchaseRepository.findByStoreAndDateRange(storeId, fromDate, toDate);
	    } else if (storeId != null) {
	        records = purchaseRepository.findByStoreIdOne(storeId);
	    } else if (fromDate != null && toDate != null) {
	        records = purchaseRepository.findByDateBetweenCustom(fromDate, toDate);
	    } else {
	        records = purchaseRepository.findAll(); // fallback
	    }

	    Map<Integer, GstSummaryResponseDto> groupedByGst = new HashMap<>();

	    for (PurchaseEntity record : records) {
	        Integer gstCode = record.getGstCode();

	        GstSummaryResponseDto dto = groupedByGst.getOrDefault(gstCode, new GstSummaryResponseDto());
	        dto.setGstCode(gstCode);
	        dto.setStoreId(record.getStoreId());
	        dto.setDate(record.getDate());
	        dto.setTaxableAmount(dto.getTaxableAmount().add(BigDecimal.valueOf(record.getTaxableAmt() != null ? record.getTaxableAmt() : 0.0)));
	        dto.setCGSTAmt(dto.getCGSTAmt().add(BigDecimal.valueOf(record.getCGSTAmt() != null ? record.getCGSTAmt() : 0.0)));
	        dto.setSGSTAmt(dto.getSGSTAmt().add(BigDecimal.valueOf(record.getSGSTAmt() != null ? record.getSGSTAmt() : 0.0)));
	        dto.setTotal(dto.getTotal().add(BigDecimal.valueOf(record.getTotal() != null ? record.getTotal() : 0.0)));

	        groupedByGst.put(gstCode, dto);
	    }

	    return new ArrayList<>(groupedByGst.values());
	}
	public Map<String, Object> getCombinedGstSummary(String storeId,String userId, String userIdStoreId, Date fromDate, Date toDate) {
	    // Get Sale GST Summary
	    List<SaleEntity> saleRecords;
	    if (storeId != null && fromDate != null && toDate != null) {
	        saleRecords = saleRepository.findByStoreAndDateRange(storeId, fromDate, toDate);
	    } else if (storeId != null) {
	        saleRecords = saleRepository.findByStoreIdOne(storeId);
	    } else if (fromDate != null && toDate != null) {
	        saleRecords = saleRepository.findByDateBetweenCustom(fromDate, toDate);
	    } else {
	        saleRecords = saleRepository.findAll(); // fallback
	    }
	    
	    if (userId != null) {
            saleRecords = saleRecords.stream()
                    .filter(s -> userId.equals(s.getUserId()))
                    .collect(Collectors.toList());
        }
	    if (userIdStoreId != null) {
            saleRecords = saleRecords.stream()
                    .filter(s -> userIdStoreId.equals(s.getUserIdStoreId()))
                    .collect(Collectors.toList());
        }


	    Map<Integer, GstSummaryResponseDto> saleGroupedByGst = new HashMap<>();
	    for (SaleEntity record : saleRecords) {
	        Integer gstCode = record.getGstCode();
	        GstSummaryResponseDto dto = saleGroupedByGst.getOrDefault(gstCode, new GstSummaryResponseDto());
	        dto.setGstCode(gstCode);
	        dto.setStoreId(record.getStoreId());
	        dto.setDate(record.getDate());
	        dto.setTaxableAmount(dto.getTaxableAmount().add(BigDecimal.valueOf(record.getTaxableAmt() != null ? record.getTaxableAmt() : 0.0)));
	        dto.setCGSTAmt(dto.getCGSTAmt().add(BigDecimal.valueOf(record.getcGSTAmt() != null ? record.getcGSTAmt() : 0.0)));
	        dto.setSGSTAmt(dto.getSGSTAmt().add(BigDecimal.valueOf(record.getsGSTAmt() != null ? record.getsGSTAmt() : 0.0)));
	        dto.setTotal(dto.getTotal().add(BigDecimal.valueOf(record.getTotal() != null ? record.getTotal() : 0.0)));
	        dto.setUserIdStoreId(record.getUserIdStoreId()); // assuming this method exists
	        dto.setUserId(record.getUserId());               // assuming this method exists

	        saleGroupedByGst.put(gstCode, dto);
	    }

	    // Get Purchase GST Summary
	    List<PurchaseEntity> purchaseRecords;
	    if (storeId != null && fromDate != null && toDate != null) {
	        purchaseRecords = purchaseRepository.findByStoreAndDateRange(storeId, fromDate, toDate);
	    } else if (storeId != null) {
	        purchaseRecords = purchaseRepository.findByStoreIdOne(storeId);
	    } else if (fromDate != null && toDate != null) {
	        purchaseRecords = purchaseRepository.findByDateBetweenCustom(fromDate, toDate);
	    } else {
	        purchaseRecords = purchaseRepository.findAll(); // fallback
	    }
	    if (userId != null) {
	    	purchaseRecords = purchaseRecords.stream()
                    .filter(s -> userId.equals(s.getUserId()))
                    .collect(Collectors.toList());
        }
	    if (userIdStoreId != null) {
            purchaseRecords = purchaseRecords.stream()
                    .filter(p -> userIdStoreId.equals(p.getUserIdStoreId()))
                    .collect(Collectors.toList());
        }

	    Map<Integer, GstSummaryResponseDto> purchaseGroupedByGst = new HashMap<>();
	    for (PurchaseEntity record : purchaseRecords) {
	        Integer gstCode = record.getGstCode();
	        GstSummaryResponseDto dto = purchaseGroupedByGst.getOrDefault(gstCode, new GstSummaryResponseDto());
	        dto.setGstCode(gstCode);
	        dto.setStoreId(record.getStoreId());
	        dto.setDate(record.getDate());
	        dto.setTaxableAmount(dto.getTaxableAmount().add(BigDecimal.valueOf(record.getTaxableAmt() != null ? record.getTaxableAmt() : 0.0)));
	        dto.setCGSTAmt(dto.getCGSTAmt().add(BigDecimal.valueOf(record.getCGSTAmt() != null ? record.getCGSTAmt() : 0.0)));
	        dto.setSGSTAmt(dto.getSGSTAmt().add(BigDecimal.valueOf(record.getSGSTAmt() != null ? record.getSGSTAmt() : 0.0)));
	        dto.setTotal(dto.getTotal().add(BigDecimal.valueOf(record.getTotal() != null ? record.getTotal() : 0.0)));
	        dto.setUserIdStoreId(record.getUserIdStoreId()); // assuming this method exists
	        dto.setUserId(record.getUserId());               // assuming this method exists

	        purchaseGroupedByGst.put(gstCode, dto);
	    }

	    // Combine both sale and purchase summaries
	    Map<String, Object> response = new HashMap<>();
	    response.put("saleGst", new ArrayList<>(saleGroupedByGst.values()));
	    response.put("purchaseGst", new ArrayList<>(purchaseGroupedByGst.values()));

	    return response;
	}


}
