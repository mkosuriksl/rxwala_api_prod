package com.kosuri.stores.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import org.springframework.data.domain.PageImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.AdminDiagnosticServiceCategory;
import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseRepository;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleHeaderEntity;
import com.kosuri.stores.dao.SaleHeaderRepository;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.dao.SaleUpdateRequestDto;
import com.kosuri.stores.dao.SaleUpdateRequestEntity;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.GstSummaryResponseDto;
import com.kosuri.stores.model.dto.ItemDetailRequest;
import com.kosuri.stores.model.dto.ItemSaleDetailRequest;
import com.kosuri.stores.model.dto.SaleInvoiceRequest;
import com.kosuri.stores.model.dto.SaleInvoiceResponseDto;
import com.kosuri.stores.model.dto.SaleUpdateFinalResponse;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.StockUpdateRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SaleHandler {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private SaleRepository saleRepository;
	@Autowired
	private StockHandler stockHandler;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private SaleHeaderRepository saleHeaderRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public void createSaleEntityFromRequest(MultipartFile reapExcelDataFile, String storeId, String emailId)
			throws Exception {

		Optional<StoreEntity> store = storeRepository.findById(storeId);
		if (store.isPresent()) {
			String ownerEmail = store.get().getOwnerEmail();
			if (!ownerEmail.equals(emailId)) {
				throw new APIException("User does not has access to upload file");
			}
		} else {
			throw new APIException("Store not found for given id");
		}

		List<SaleEntity> saleArrayList = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
			SaleEntity tempSale = new SaleEntity();
			XSSFRow row = worksheet.getRow(i);
			tempSale.setDocNumber(
					String.valueOf(BigDecimal.valueOf(row.getCell(0).getNumericCellValue()).toBigInteger()));
			tempSale.setReadableDocNo(row.getCell(1).getStringCellValue());
			tempSale.setDate(row.getCell(2).getDateCellValue());
			tempSale.setTime(row.getCell(3).getDateCellValue());
			tempSale.setCustCode(row.getCell(4).getStringCellValue());
			tempSale.setCustName(row.getCell(5).getStringCellValue());

			tempSale.setPatientName(row.getCell(6).getStringCellValue());
			tempSale.setCreatedUser(
					String.valueOf(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()).toBigInteger()));
			try {
				tempSale.setItemCode(row.getCell(8).getStringCellValue());
			} catch (Exception e) {
				tempSale.setItemCode(String.valueOf(row.getCell(8).getNumericCellValue()));
			}
			try {
				tempSale.setItemName(row.getCell(9).getStringCellValue());
			} catch (Exception e) {
				tempSale.setItemName(String.valueOf(row.getCell(9).getNumericCellValue()));
			}

			try {
				tempSale.setBatchNo(row.getCell(10).getStringCellValue());
			} catch (Exception e) {
				tempSale.setBatchNo(
						String.valueOf(BigDecimal.valueOf(row.getCell(10).getNumericCellValue()).toBigInteger()));
			}
			tempSale.setExpiryDate(row.getCell(11).getDateCellValue());
			tempSale.setMfacCode(row.getCell(12).getStringCellValue());
			tempSale.setMfacName(row.getCell(13).getStringCellValue());
			tempSale.setCatCode(row.getCell(14).getStringCellValue());
			tempSale.setCatName(row.getCell(15).getStringCellValue());
			tempSale.setBrandName(row.getCell(16).getStringCellValue());
			tempSale.setPacking(row.getCell(17).getStringCellValue());
			tempSale.setQtyBox(row.getCell(18).getNumericCellValue());
			tempSale.setQty(BigDecimal.valueOf(row.getCell(19).getNumericCellValue()).doubleValue());
			tempSale.setSchQty((int) row.getCell(20).getNumericCellValue());
			tempSale.setSchDisc(row.getCell(21).getNumericCellValue());
			tempSale.setSaleRate(row.getCell(22).getNumericCellValue());
			tempSale.setmRP(BigDecimal.valueOf(row.getCell(23).getNumericCellValue()).doubleValue());
			tempSale.setSaleValue(BigDecimal.valueOf(row.getCell(24).getNumericCellValue()).doubleValue());
			tempSale.setDiscPerct(row.getCell(25).getNumericCellValue());
			tempSale.setDiscValue(row.getCell(26).getNumericCellValue());
			tempSale.setTaxableAmt(row.getCell(27).getNumericCellValue());
			tempSale.setcGSTPer((int) row.getCell(28).getNumericCellValue());
			tempSale.setcGSTAmt(row.getCell(29).getNumericCellValue());
			tempSale.setsGSTPer((int) row.getCell(30).getNumericCellValue());
			tempSale.setsGSTAmt(row.getCell(31).getNumericCellValue());
			tempSale.setiGSTPer((int) row.getCell(32).getNumericCellValue());
			tempSale.setiGSTAmt(row.getCell(33).getNumericCellValue());
			tempSale.setCessPer((int) row.getCell(34).getNumericCellValue());
			tempSale.setCessAmt(row.getCell(35).getNumericCellValue());
			tempSale.setAddCessPer((int) row.getCell(36).getNumericCellValue());
			tempSale.setAddCessAmt(row.getCell(37).getNumericCellValue());
			tempSale.setTotal(row.getCell(38).getNumericCellValue());
			tempSale.setRoundOff(row.getCell(39).getNumericCellValue());
			try {
				tempSale.setSuppBillNo(row.getCell(40).getStringCellValue());
			} catch (Exception e) {
				tempSale.setSuppBillNo(String.valueOf(row.getCell(41).getNumericCellValue()));
			}

			tempSale.setSuppCode(row.getCell(42).getStringCellValue());
			tempSale.setSuppName(row.getCell(43).getStringCellValue());
			tempSale.setProfessional(row.getCell(44).getStringCellValue());

			tempSale.setMobile(
					String.valueOf(BigDecimal.valueOf(row.getCell(45).getNumericCellValue()).toBigInteger()));
			try {
				tempSale.setLcCode(row.getCell(46).getStringCellValue());
			} catch (Exception e) {
				tempSale.setLcCode(String.valueOf(row.getCell(46).getNumericCellValue()));
			}

			tempSale.setPurRate(row.getCell(47).getNumericCellValue());
			tempSale.setPurRateWithGsT(row.getCell(48).getNumericCellValue());
			tempSale.setStoreId(storeId);

			saleArrayList.add(tempSale);
		}

		saleRepository.saveAll(saleArrayList);

		for (SaleEntity saleEntity : saleArrayList) {
			updateStock(saleEntity, emailId);
		}
	}

	private void updateStock(SaleEntity saleEntity, String email) throws Exception {
		StockUpdateRequest stockUpdateRequest = new StockUpdateRequest();
		stockUpdateRequest.setExpiryDate(saleEntity.getExpiryDate());
		stockUpdateRequest.setBatch(saleEntity.getBatchNo());
		stockUpdateRequest.setStockUpdateRequestType(StockUpdateRequestType.SALE);
		stockUpdateRequest.setQtyPerBox(saleEntity.getQtyBox());
		stockUpdateRequest.setBalLooseQuantity(saleEntity.getQty());
		stockUpdateRequest.setItemCode(saleEntity.getItemCode());
		stockUpdateRequest.setItemName(saleEntity.getItemName());
		stockUpdateRequest.setMfName(saleEntity.getMfacName());
		stockUpdateRequest.setManufacturer(saleEntity.getMfacCode());
		stockUpdateRequest.setStoreId(saleEntity.getStoreId());
		stockUpdateRequest.setMrpPack(saleEntity.getmRP() * saleEntity.getQtyBox());
		stockUpdateRequest.setSupplierName(saleEntity.getSuppName());
		stockUpdateRequest.setUpdatedBy(email);

		stockHandler.updateStock(stockUpdateRequest);
	}

//	public List<SaleEntity> saveSalesInvoices(List<SaleInvoiceRequest> saleInvoiceRequests) {
//		List<SaleEntity> purchases = maptoRequest(saleInvoiceRequests);
//		saleRepository.saveAll(purchases);
//		return purchases;
//	}
//
//	private List<SaleEntity> maptoRequest(List<SaleInvoiceRequest> saleInvoiceRequests) {
//		return saleInvoiceRequests.stream().map(request -> {
//			SaleEntity entity = new SaleEntity();
//			entity.setDocNumber(request.getDoc_Number());
//			entity.setReadableDocNo(request.getReadableDocNo());
//			entity.setDate(request.getDate());
//			entity.setTime(request.getTime());
//			entity.setCustCode(request.getCustCode());
//			entity.setCustName(request.getCustName());
//			entity.setPatientName(request.getPatientName());
//			entity.setCreatedUser(request.getCreatedUser());
//			entity.setItemCode(request.getItemCode());
//			entity.setItemName(request.getItemName());
//			entity.setBatchNo(request.getBatchNo());
//			entity.setExpiryDate(request.getExpiryDate());
//			entity.setMfacCode(request.getMfacCode());
//			entity.setMfacName(request.getMfacName());
//			entity.setCatCode(request.getCatCode());
//			entity.setCatName(request.getCatName());
//			entity.setBrandName(request.getBrandName());
//			entity.setPacking(request.getPacking());
//			entity.setQtyBox(request.getQtyBox());
//			entity.setQty(request.getQty());
//			entity.setSchQty(request.getSchQty());
//			entity.setSchDisc(request.getSchDisc());
//			entity.setSaleRate(request.getSaleRate());
//			entity.setmRP(request.getMRP());
//			entity.setSaleValue(request.getSaleValue());
//			entity.setDiscPerct(request.getDiscPerct());
//			entity.setDiscValue(request.getDiscValue());
//			entity.setTaxableAmt(request.getTaxableAmt());
//			entity.setcGSTPer(request.getCGSTPer());
//			entity.setsGSTPer(request.getSGSTPer());
//			entity.setcGSTAmt(request.getCGSTAmt());
//			entity.setsGSTAmt(request.getSGSTAmt());
//			entity.setiGSTPer(request.getIGSTPer());
//			entity.setiGSTAmt(request.getIGSTAmt());
//			entity.setSuppCode(request.getSuppCode());
//			entity.setSuppName(request.getSuppName());
//			entity.setTotal(request.getTotal());
//			entity.setCessPer(request.getCessPer());
//			entity.setCessAmt(request.getCessAmt());
//			entity.setAddCessPer(request.getAddCessPer());
//			entity.setAddCessAmt(request.getAddCessAmt());
//			entity.setRoundOff(request.getRoundOff());
//			entity.setSuppBillNo(request.getSuppBillNo());
//			entity.setProfessional(request.getProfessional());
//			entity.setMobile(request.getMobile());
//			entity.setLcCode(request.getLcCode());
//			entity.setPurRate(request.getPurRate());
//			entity.setPurRateWithGsT(request.getPurRateWithGsT());
//			entity.setStoreId(request.getStoreId());
//			entity.setSaleMode(request.getSaleMode());
//			return entity;
//		}).toList();
//	}

	public List<SaleEntity> saveSalesInvoicesEn(SaleInvoiceRequest saleInvoiceRequests) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		Optional<StoreEntity> storeEntityOptional = storeRepository.findById(saleInvoiceRequests.getStoreId());

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

		for (ItemSaleDetailRequest detail : saleInvoiceRequests.getDetailRequests()) {
			totalTaxable += detail.getTaxableAmt() != null ? detail.getTaxableAmt() : 0.0;
			totalCgst += detail.getCGSTAmt() != null ? detail.getCGSTAmt() : 0.0;
			totalSgst += detail.getSGSTAmt() != null ? detail.getSGSTAmt() : 0.0;
			totalIgst += detail.getIGSTAmt() != null ? detail.getIGSTAmt() : 0.0;
			totalCess += detail.getCessAmt() != null ? detail.getCessAmt() : 0.0;
			grandTotal += detail.getTotal() != null ? detail.getTotal() : 0.0;
		}
		SaleHeaderEntity header = new SaleHeaderEntity();
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String docNumber = "DOC" + timeStamp;
		header.setDocNumber(docNumber);
		header.setDate(new Date());
		header.setCustCode(saleInvoiceRequests.getCustCode());
		header.setCustName(saleInvoiceRequests.getCustName());
		header.setStoreId(saleInvoiceRequests.getStoreId());
//		    header.setTaxableAmt(totalTaxable);
//		    header.setCGSTAmt(totalCgst);
//		    header.setSGSTAmt(totalSgst);
//		    header.setIGSTAmt(totalIgst);
//		    header.setCessAmt(totalCess);
//		    header.setTotal(grandTotal);
		header.setUserIdStoreId(login.get().getUserId() + "_" + saleInvoiceRequests.getStoreId());

		saleHeaderRepository.save(header);
		List<SaleEntity> purchases = maptoRequestEn(saleInvoiceRequests, docNumber);
		saleRepository.saveAll(purchases);
		return purchases;
	}

	private List<SaleEntity> maptoRequestEn(SaleInvoiceRequest saleInvoiceRequests, String docNumber) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		AtomicInteger counter = new AtomicInteger(1);
		return saleInvoiceRequests.getDetailRequests().stream().map(request -> {
			SaleEntity entity = new SaleEntity();
			entity.setDocNumber(docNumber);
			String lineId = String.format("%s_%04d", docNumber, counter.getAndIncrement());
			entity.setDocNumberLineId(lineId);
			entity.setDate(new Date());
			entity.setCustCode(saleInvoiceRequests.getCustCode());
			entity.setCustName(saleInvoiceRequests.getCustName());
			entity.setStoreId(saleInvoiceRequests.getStoreId());
			entity.setReadableDocNo(request.getReadableDocNo());
			entity.setTime(request.getTime());
			entity.setPatientName(request.getPatientName());
			entity.setCreatedUser(request.getCreatedUser());
			StockEntity stock = stockRepository.findByUserIdStoreIdItemCodeAndBatch(
					login.get().getUserId() + "_" + saleInvoiceRequests.getStoreId() + "_" + request.getItemCode(),
					request.getBatchNo());

//			if (stock != null) {
//				entity.setItemCode(stock.getItemCode());
//
//				Double currentBalQty = stock.getBalQuantity();
//				Double qtyBoxPurchased = request.getQtyBox();
//
//				Double newcurrentBalQty = currentBalQty - qtyBoxPurchased;
//				stock.setBalQuantity(newcurrentBalQty);
//
//				PurchaseEntity purchaseEntity = purchaseRepository.findByUserIdStoreIdItemCodeAndBatchNo(
//						login.get().getUserId() + "_" + saleInvoiceRequests.getStoreId() + "_" + request.getItemCode(),
//						request.getBatchNo());
//
//				Double packQty = (newcurrentBalQty / purchaseEntity.getQty());
//				stock.setBalPackQuantity(packQty);
//				stock.setBalLooseQuantity(packQty);
//
//				stockRepository.save(stock);
//			}
			if (stock != null) {
				entity.setItemCode(stock.getItemCode());

				Double currentBalQty = stock.getBalQuantity(); // 175.0
				Double qtyBoxPurchased = request.getQtyBox(); // e.g., 12 10
				Double looseQty = request.getQty();

//			    Double newcurrentBalQty = currentBalQty - qtyBoxPurchased; // 175-12
//			    stock.setBalQuantity(newcurrentBalQty);//165

				PurchaseEntity purchaseEntity = purchaseRepository.findByUserIdStoreIdItemCodeAndBatchNo(
						login.get().getUserId() + "_" + saleInvoiceRequests.getStoreId() + "_" + request.getItemCode(),
						request.getBatchNo());

				// Assume each pack contains 12 units (strip size)
				Double stripSize = purchaseEntity.getQty(); // e.g., 10
				Double newcurrentQtyBox = (qtyBoxPurchased * stripSize);
				Double totalSoldqty = newcurrentQtyBox + looseQty;
				Double totalBalanceQty = currentBalQty - totalSoldqty;
				stock.setBalQuantity(totalBalanceQty);// 165

				// Calculate number of full strips
				int fullStrips = (int) (totalBalanceQty / stripSize); // 50 / 12 = 4
				// Calculate remaining loose units
				double looseUnits = totalBalanceQty % stripSize; // 50 % 12 = 2

				stock.setBalPackQuantity((double) fullStrips); // 4
				stock.setBalLooseQuantity(looseUnits); // 2

				stockRepository.save(stock);
			}

			entity.setItemCode(request.getItemCode());
			entity.setItemName(request.getItemName());
			entity.setBatchNo(request.getBatchNo());
			entity.setExpiryDate(request.getExpiryDate());
			entity.setMfacCode(request.getMfacCode());
			entity.setMfacName(request.getMfacName());
			entity.setCatCode(request.getCatCode());
			entity.setCatName(request.getCatName());
			entity.setBrandName(request.getBrandName());
			entity.setPacking(request.getPacking());
//			entity.setQtyBox(request.getQtyBox());
			entity.setQty(request.getQty());
			entity.setQtyBox(request.getQtyBox());
			entity.setSchQty(request.getSchQty());
			entity.setSchDisc(request.getSchDisc());
			entity.setSaleRate(request.getSaleRate());
			entity.setmRP(request.getMRP());
			entity.setSaleValue(request.getSaleValue());
			entity.setDiscPerct(request.getDiscPerct());
			entity.setDiscValue(request.getDiscValue());
			entity.setTaxableAmt(request.getTaxableAmt());
			entity.setcGSTPer(request.getCGSTPer());
			entity.setsGSTPer(request.getSGSTPer());
			entity.setcGSTAmt(request.getCGSTAmt());
			entity.setsGSTAmt(request.getSGSTAmt());
			entity.setiGSTPer(request.getIGSTPer());
			entity.setiGSTAmt(request.getIGSTAmt());
			entity.setSuppCode(request.getSuppCode());
			entity.setSuppName(request.getSuppName());
			entity.setTotal(request.getTotal());
			entity.setCessPer(request.getCessPer());
			entity.setCessAmt(request.getCessAmt());
			entity.setAddCessPer(request.getAddCessPer());
			entity.setAddCessAmt(request.getAddCessAmt());
			entity.setRoundOff(request.getRoundOff());
			entity.setSuppBillNo(request.getSuppBillNo());
			entity.setProfessional(request.getProfessional());
			entity.setMobile(request.getMobile());
			entity.setLcCode(request.getLcCode());
			entity.setPurRate(request.getPurRate());
			entity.setPurRateWithGsT(request.getPurRateWithGsT());
			entity.setSaleMode(request.getSaleMode());
			entity.setGstCode(request.getGstCode());
			entity.setAfterDiscount(request.getAfterDiscount());
			entity.setTotalPurchasePrice(request.getTotalPurchasePrice());
			entity.setProfitOrLoss(request.getProfitOrLoss());
			entity.setUserId(login.get().getUserId());
			entity.setUserIdStoreIdItemCode(
					login.get().getUserId() + "_" + saleInvoiceRequests.getStoreId() + "_" + request.getItemCode());
			entity.setUserIdStoreId(login.get().getUserId() + "_" + saleInvoiceRequests.getStoreId());
			return entity;
		}).toList();
	}

//	public List<SaleInvoiceResponseDto> getSale(String docNumber, String custName, String storeId, Date fromDate,
//			Date toDate, String userIdStoreId) {
//		Specification<SaleEntity> spec = (root, query, cb) -> {
//			List<Predicate> predicates = new ArrayList<>();
//
//			if (docNumber != null) {
//				predicates.add(cb.equal(root.get("docNumber"), docNumber));
//			}
//			if (custName != null) {
//				predicates.add(cb.equal(root.get("custName"), custName));
//			}
//			if (storeId != null) {
//				predicates.add(cb.equal(root.get("storeId"), storeId));
//			}
//			if (fromDate != null && toDate != null) {
//				predicates.add(cb.between(root.get("date"), fromDate, toDate));
//			} else if (fromDate != null) {
//				predicates.add(cb.equal(root.get("date"), fromDate));
//			}
//			if (userIdStoreId != null) {
//				predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
//			}
//
//			return cb.and(predicates.toArray(new Predicate[0]));
//		};
//
//		List<SaleEntity> saleEntities = saleRepository.findAll(spec);
//
//		// Convert entity list to DTO list
//		return saleEntities.stream().map(p -> new SaleInvoiceResponseDto(p.getDocNumber(), p.getDate(), p.getCustName(),
//				p.getStoreId(), p.getUserIdStoreId())).collect(Collectors.toList());
//	}

	public Page<SaleInvoiceResponseDto> getSale(String docNumber, String custName, String storeId,
			Date fromDate, Date toDate, String userIdStoreId, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

// Main query for result list
		CriteriaQuery<SaleEntity> query = cb.createQuery(SaleEntity.class);
		Root<SaleEntity> root = query.from(SaleEntity.class);
		List<Predicate> predicates = buildPredicates(cb, root, docNumber, custName, storeId, fromDate, toDate,
				userIdStoreId);
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		TypedQuery<SaleEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		List<SaleEntity> saleEntities = typedQuery.getResultList();

// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<SaleEntity> countRoot = countQuery.from(SaleEntity.class);
		List<Predicate> countPredicates = buildPredicates(cb, countRoot, docNumber, custName, storeId, fromDate, toDate,
				userIdStoreId);
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		List<SaleInvoiceResponseDto> dtos = saleEntities.stream().map(p -> new SaleInvoiceResponseDto(p.getDocNumber(),
				p.getDate(), p.getCustName(), p.getStoreId(), p.getUserIdStoreId())).collect(Collectors.toList());

		return new PageImpl<>(dtos, pageable, total);
	}

	private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<SaleEntity> root, String docNumber,
			String custName, String storeId, Date fromDate, Date toDate, String userIdStoreId) {
		List<Predicate> predicates = new ArrayList<>();

		if (docNumber != null) {
			predicates.add(cb.equal(root.get("docNumber"), docNumber));
		}
		if (custName != null) {
			predicates.add(cb.equal(root.get("custName"), custName));
		}
		if (storeId != null) {
			predicates.add(cb.equal(root.get("storeId"), storeId));
		}
		if (fromDate != null && toDate != null) {
			predicates.add(cb.between(root.get("date"), fromDate, toDate));
		} else if (fromDate != null) {
			predicates.add(cb.equal(root.get("date"), fromDate));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}

		return predicates;
	}

	public Map<String, Object> getCustNameBySearch(String custName) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<SaleEntity> root = query.from(SaleEntity.class);

		List<Predicate> predicates = new ArrayList<>();
		if (custName != null && !custName.isBlank()) {
			predicates.add(cb.like(cb.lower(root.get("custName")), "%" + custName.toLowerCase() + "%"));
		}

		query.multiselect(root.get("custName"));
		query.where(cb.and(predicates.toArray(new Predicate[0])));

		List<Object[]> resultList = entityManager.createQuery(query).getResultList();

		List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
			Map<String, Object> map = new HashMap<>();
			map.put("custName", obj[0]);
			return map;
		}).collect(Collectors.toList());

		return Map.of("message", "Customer Name fetched successfully", "status", true, "totalCount",
				responseData.size(), // ✅ Total count added
				"data", responseData);
	}

//	public List<SaleEntity> getSaleInovices(String docNumber, String userIdStoreId) {
//		Specification<SaleEntity> spec = (root, query, cb) -> {
//			List<Predicate> predicates = new ArrayList<>();
//
//			if (docNumber != null) {
//				predicates.add(cb.equal(root.get("docNumber"), docNumber));
//			}
//			if (userIdStoreId != null) {
//				predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
//			}
//
//			return cb.and(predicates.toArray(new Predicate[0]));
//		};
//
//		return saleRepository.findAll(spec);
//	}
	public Page<SaleEntity> getSaleInvoices(String docNumber, String userIdStoreId, Pageable pageable) {
	    Specification<SaleEntity> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (docNumber != null && !docNumber.isEmpty()) {
	            predicates.add(cb.equal(root.get("docNumber"), docNumber));
	        }
	        if (userIdStoreId != null && !userIdStoreId.isEmpty()) {
	            predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    return saleRepository.findAll(spec, pageable);
	}


	public SaleUpdateFinalResponse updatePurchasesByInvoice(SaleUpdateRequestDto requestDto) {

		List<SaleEntity> invoiceRecords = saleRepository.findByDocNumber(requestDto.getDoc_Number());

		if (invoiceRecords.isEmpty()) {
			SaleUpdateFinalResponse response = new SaleUpdateFinalResponse();
			response.setDoc_Number(requestDto.getDoc_Number());
			response.setStatus("error");
			response.setMessage("Document number '" + requestDto.getDoc_Number() + "' not found.");
			response.setUpdated(Collections.emptyList());
			return response;
		}

		List<SaleEntity> updatedList = new ArrayList<>();
		List<String> errorMessages = new ArrayList<>();

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}

		for (SaleUpdateRequestEntity request : requestDto.getUpdates()) {

			String uniqueKey = request.getUserIdStoreIdItemCode();

			Optional<SaleEntity> existing = saleRepository.findByUserIdStoreIdItemCode(uniqueKey)
					.filter(e -> !e.getDocNumberLineId().equals(request.getDocNumberLineId()));

			if (existing.isPresent()) {
				errorMessages.add("Duplicate entry found for userIdStoreIdItemCode: '" + uniqueKey + "'");
				continue;
			}

			SaleEntity entity = new SaleEntity();
			entity.setDocNumberLineId(request.getDocNumberLineId());
			entity.setDocNumber(invoiceRecords.get(0).getDocNumber());
			entity.setReadableDocNo(request.getReadableDocNo());
			entity.setDate(request.getDate());
			entity.setTime(request.getTime());
			entity.setCustCode(request.getCustCode());
			entity.setCustName(request.getCustName());
			entity.setPatientName(request.getPatientName());
			entity.setCreatedUser(request.getCreatedUser());
			entity.setItemCode(request.getItemCode());
			entity.setItemName(request.getItemName());
			entity.setBatchNo(request.getBatchNo());
			entity.setExpiryDate(request.getExpiryDate());
			entity.setMfacCode(request.getMfacCode());
			entity.setMfacName(request.getMfacName());
			entity.setCatCode(request.getCatCode());
			entity.setCatName(request.getCatName());
			entity.setBrandName(request.getBrandName());
			entity.setPacking(request.getPacking());
			entity.setQtyBox(request.getQtyBox());
			entity.setQty(request.getQty());
			entity.setSchQty(request.getSchQty());
			entity.setSchDisc(request.getSchDisc());
			entity.setSaleRate(request.getSaleRate());
			entity.setmRP(request.getMRP());
			entity.setSaleValue(request.getSaleValue());
			entity.setDiscPerct(request.getDiscPerct());
			entity.setDiscValue(request.getDiscValue());
			entity.setTaxableAmt(request.getTaxableAmt());
			entity.setcGSTPer(request.getCGSTPer());
			entity.setsGSTPer(request.getSGSTPer());
			entity.setcGSTAmt(request.getCGSTAmt());
			entity.setsGSTAmt(request.getSGSTAmt());
			entity.setiGSTPer(request.getIGSTPer());
			entity.setiGSTAmt(request.getIGSTAmt());
			entity.setSuppCode(request.getSuppCode());
			entity.setSuppName(request.getSuppName());
			entity.setTotal(request.getTotal());
			entity.setCessPer(request.getCessPer());
			entity.setCessAmt(request.getCessAmt());
			entity.setAddCessPer(request.getAddCessPer());
			entity.setAddCessAmt(request.getAddCessAmt());
			entity.setRoundOff(request.getRoundOff());
			entity.setSuppBillNo(request.getSuppBillNo());
			entity.setProfessional(request.getProfessional());
			entity.setMobile(request.getMobile());
			entity.setLcCode(request.getLcCode());
			entity.setPurRate(request.getPurRate());
			entity.setPurRateWithGsT(request.getPurRateWithGsT());
			entity.setStoreId(request.getStoreId());
			entity.setSaleMode(request.getSaleMode());
			entity.setAfterDiscount(request.getAfterDiscount());
			entity.setTotalPurchasePrice(request.getTotalPurchasePrice());
			entity.setProfitOrLoss(request.getProfitOrLoss());
			entity.setUserId(login.get().getUserId());
			entity.setUserIdStoreId(request.getUserIdStoreId());
			entity.setUserIdStoreIdItemCode(request.getUserIdStoreIdItemCode());
			try {
				saleRepository.save(entity);
				updatedList.add(modelMapper.map(entity, SaleEntity.class));
			} catch (DataIntegrityViolationException e) {
				Throwable rootCause = ExceptionUtils.getRootCause(e);
				String errorMsg = rootCause != null ? rootCause.getMessage() : e.getMessage();
				errorMessages.add("Duplicate entry for userIdStoreIdItemCode '" + uniqueKey + "': " + errorMsg);
			}
		}

		SaleUpdateFinalResponse finalResponse = new SaleUpdateFinalResponse();
		finalResponse.setDoc_Number(requestDto.getDoc_Number());
		finalResponse.setUpdated(updatedList);

		if (errorMessages.isEmpty()) {
			finalResponse.setMessage(updatedList.size() + " records processed successfully.");
			finalResponse.setStatus("success");
		} else {
			finalResponse.setMessage(updatedList.size() + " records updated. " + errorMessages.size()
					+ " skipped due to errors: " + String.join("; ", errorMessages));
			finalResponse.setStatus("partial_success");
		}

		return finalResponse;

	}

	public Map<String, Object> updateSale(List<SaleEntity> entities) {
		int successCount = 0;
		List<String> failedKeys = new ArrayList<>();
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		for (SaleEntity entity : entities) {
//			Optional<SaleEntity> optional = saleRepository
//					.findByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());
			Optional<SaleEntity> optional = saleRepository.findByDocNumberLineId(entity.getDocNumberLineId());
			if (optional.isPresent()) {
				SaleEntity existing = optional.get();

				// Copy required fields from existing entity to the new one
				entity.setDocNumberLineId(existing.getDocNumberLineId());
				entity.setDocNumber(existing.getDocNumber());
				entity.setReadableDocNo(entities.get(0).getReadableDocNo());
				entity.setDate(entities.get(0).getDate());
				entity.setTime(entities.get(0).getTime());
				entity.setCustCode(entities.get(0).getCustCode());
				entity.setCustName(entities.get(0).getCustName());
				entity.setPatientName(entities.get(0).getPatientName());
				entity.setCreatedUser(entities.get(0).getCreatedUser());
//				entity.setItemCode(entities.get(0).getItemCode());
				StockEntity stock = stockRepository.findByItemCode(entities.get(0).getItemCode());

				if (stock != null) {
					entity.setItemCode(stock.getItemCode());
					Double currentBalQty = stock.getBalQuantity(); // current box quantity
					Double currentBalPackQty = stock.getBalPackQuantity(); // how many items per box
					Double qtyBoxPurchased = entities.get(0).getQtyBox(); // purchased boxes
					Double looseQtyRemaining = (currentBalPackQty * currentBalQty);
					Double looseQtyRemaining2 = looseQtyRemaining - qtyBoxPurchased;
					Double looseQtyRemaining3 = (looseQtyRemaining2 / currentBalQty);
					stock.setBalLooseQuantity(looseQtyRemaining3 * currentBalQty);

					int packQtyToSet = looseQtyRemaining3.intValue();
					stock.setBalPackQuantity((double) packQtyToSet);
					stockRepository.save(stock);

				}
				entity.setItemName(entities.get(0).getItemName());
				entity.setBatchNo(entities.get(0).getBatchNo());
				entity.setExpiryDate(entities.get(0).getExpiryDate());
				entity.setMfacCode(entities.get(0).getMfacCode());
				entity.setMfacName(entities.get(0).getMfacName());
				entity.setCatCode(entities.get(0).getCatCode());
				entity.setCatName(entities.get(0).getCatName());
				entity.setBrandName(entities.get(0).getBrandName());
				entity.setPacking(entities.get(0).getPacking());
				entity.setQtyBox(entities.get(0).getQtyBox());
				entity.setQty(entities.get(0).getQty());
				entity.setSchQty(entities.get(0).getSchQty());
				entity.setSchDisc(entities.get(0).getSchDisc());
				entity.setSaleRate(entities.get(0).getSaleRate());
				entity.setmRP(entities.get(0).getmRP());
				entity.setSaleValue(entities.get(0).getSaleValue());
				entity.setDiscPerct(entities.get(0).getDiscPerct());
				entity.setDiscValue(entities.get(0).getDiscValue());
				entity.setTaxableAmt(entities.get(0).getTaxableAmt());
				entity.setcGSTPer(entities.get(0).getcGSTPer());
				entity.setsGSTPer(entities.get(0).getsGSTPer());
				entity.setcGSTAmt(entities.get(0).getcGSTAmt());
				entity.setsGSTAmt(entities.get(0).getsGSTAmt());
				entity.setiGSTPer(entities.get(0).getiGSTPer());
				entity.setiGSTAmt(entities.get(0).getiGSTAmt());
				entity.setSuppCode(entities.get(0).getSuppCode());
				entity.setSuppName(entities.get(0).getSuppName());
				entity.setTotal(entities.get(0).getTotal());
				entity.setCessPer(entities.get(0).getCessPer());
				entity.setCessAmt(entities.get(0).getCessAmt());
				entity.setAddCessPer(entities.get(0).getAddCessPer());
				entity.setAddCessAmt(entities.get(0).getAddCessAmt());
				entity.setRoundOff(entities.get(0).getRoundOff());
				entity.setSuppBillNo(entities.get(0).getSuppBillNo());
				entity.setProfessional(entities.get(0).getProfessional());
				entity.setMobile(entities.get(0).getMobile());
				entity.setLcCode(entities.get(0).getLcCode());
				entity.setPurRate(entities.get(0).getPurRate());
				entity.setPurRateWithGsT(entities.get(0).getPurRateWithGsT());
				entity.setStoreId(entities.get(0).getStoreId());
				entity.setSaleMode(entities.get(0).getSaleMode());
				entity.setGstCode(entities.get(0).getGstCode());
				entity.setAfterDiscount(entities.get(0).getAfterDiscount());
				entity.setTotalPurchasePrice(entities.get(0).getTotalPurchasePrice());
				entity.setProfitOrLoss(entities.get(0).getProfitOrLoss());
//				entity.setUserId(login.get().getUserId());
//				entity.setUserIdStoreId(entities.get(0).getUserIdStoreId());
//				entity.setUserIdStoreIdItemCode(entities.get(0).getUserIdStoreIdItemCode());

				saleRepository.save(entity);
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
		List<SaleEntity> records;

		if (storeId != null && fromDate != null && toDate != null) {
			records = saleRepository.findByStoreAndDateRange(storeId, fromDate, toDate);
		} else if (storeId != null) {
			records = saleRepository.findByStoreIdOne(storeId);
		} else if (fromDate != null && toDate != null) {
			records = saleRepository.findByDateBetweenCustom(fromDate, toDate);
		} else {
			records = saleRepository.findAll(); // fallback
		}

		Map<Integer, GstSummaryResponseDto> groupedByGst = new HashMap<>();

		for (SaleEntity record : records) {
			Integer gstCode = record.getGstCode();

			GstSummaryResponseDto dto = groupedByGst.getOrDefault(gstCode, new GstSummaryResponseDto());
			dto.setGstCode(gstCode);
			dto.setStoreId(record.getStoreId());
			dto.setDate(record.getDate());
			dto.setTaxableAmount(dto.getTaxableAmount()
					.add(BigDecimal.valueOf(record.getTaxableAmt() != null ? record.getTaxableAmt() : 0.0)));
			dto.setCGSTAmt(
					dto.getCGSTAmt().add(BigDecimal.valueOf(record.getcGSTAmt() != null ? record.getcGSTAmt() : 0.0)));
			dto.setSGSTAmt(
					dto.getSGSTAmt().add(BigDecimal.valueOf(record.getsGSTAmt() != null ? record.getsGSTAmt() : 0.0)));
			dto.setTotal(dto.getTotal().add(BigDecimal.valueOf(record.getTotal() != null ? record.getTotal() : 0.0)));

			groupedByGst.put(gstCode, dto);
		}

		return new ArrayList<>(groupedByGst.values());
	}
}
