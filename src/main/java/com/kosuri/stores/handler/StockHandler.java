package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosuri.stores.dao.ItemCodeMaster;
import com.kosuri.stores.dao.ItemOfferEntity;
import com.kosuri.stores.dao.ItemOfferRepository;
import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.OrderDetailsDto;
import com.kosuri.stores.model.dto.OrderRequestDto;
import com.kosuri.stores.model.dto.StockMrpUpdateRequest;
import com.kosuri.stores.model.dto.StockReportDTO;
import com.kosuri.stores.model.dto.StockReportDTOEn;
import com.kosuri.stores.model.dto.StockRequest;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.StockUpdateRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class StockHandler {

	@Autowired
	StockRepository stockRepository;
	@Autowired
	StoreRepository storeRepository;
	@Autowired
	private TabStoreRepository tabStoreRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ItemOfferRepository itemOfferRepository;

	public void updateStock(StockUpdateRequest stockUpdateRequest) throws Exception {
		try {
			StockEntity stock = stockRepository.findByMfNameAndItemNameAndBatchAndStoreIdAndSupplierName(
					stockUpdateRequest.getMfName(), stockUpdateRequest.getItemName(), stockUpdateRequest.getBatch(),
					stockUpdateRequest.getStoreId(), stockUpdateRequest.getSupplierName());

			if (stock != null) {
				Double currBalLooseQuantity;
				Double currBalPackQuantity;
				Double curBalQuantity = stock.getBalQuantity();

				if (stockUpdateRequest.getStockUpdateRequestType() == StockUpdateRequestType.PURCHASE) {
					curBalQuantity += stockUpdateRequest.getPackQuantity() * stockUpdateRequest.getQtyPerBox()
							+ stockUpdateRequest.getBalLooseQuantity();
				} else {
					curBalQuantity -= stockUpdateRequest.getBalLooseQuantity();
				}

				if (curBalQuantity < 0) {
					throw new APIException(
							String.format("Not Enough stock for Mf Name %s, item name %s, Batch %s Supplier Name %s",
									stockUpdateRequest.getMfName(), stockUpdateRequest.getItemName(),
									stockUpdateRequest.getBatch(), stockUpdateRequest.getSupplierName()));
				}

				currBalPackQuantity = Math.floor(curBalQuantity / stockUpdateRequest.getQtyPerBox());
				currBalLooseQuantity = curBalQuantity - (currBalPackQuantity * stockUpdateRequest.getQtyPerBox());

				Double stockValueMrp = currBalPackQuantity * stockUpdateRequest.getMrpPack()
						+ currBalLooseQuantity * (stockUpdateRequest.getMrpPack() / stockUpdateRequest.getQtyPerBox());
				Double purRatePerPackAfterGST = stock.getPurRatePerPackAfterGST();
				Double stockValuePurRate = currBalPackQuantity * purRatePerPackAfterGST
						+ currBalLooseQuantity * (purRatePerPackAfterGST / stockUpdateRequest.getQtyPerBox());

				stock.setBalPackQuantity(currBalPackQuantity);
				stock.setBalLooseQuantity(currBalLooseQuantity);
				stock.setBalQuantity(curBalQuantity);
				stock.setMrpPack(stockUpdateRequest.getMrpPack());
				stock.setStockValueMrp(stockValueMrp);
				stock.setStockValuePurrate(stockValuePurRate);
				stock.setUpdatedBy(stockUpdateRequest.getUpdatedBy());
				stock.setUpdatedAt(LocalDateTime.now());
				stockRepository.save(stock);

			} else {

				if (stockUpdateRequest.getStockUpdateRequestType() == StockUpdateRequestType.SALE) {
					throw new APIException(String.format(
							"Corresponding stock entity doesn't exist for Mf Name %s, item name %s, Batch %s Supplier Name %s",
							stockUpdateRequest.getMfName(), stockUpdateRequest.getItemName(),
							stockUpdateRequest.getBatch(), stockUpdateRequest.getSupplierName()));
				}
				StockEntity s = new StockEntity();
				s.setItemName(stockUpdateRequest.getItemName());
				s.setBatch(stockUpdateRequest.getBatch());
				s.setManufacturer(stockUpdateRequest.getManufacturer());
				s.setMfName(stockUpdateRequest.getMfName());
				s.setSupplierName(stockUpdateRequest.getSupplierName());
				s.setItemCode(stockUpdateRequest.getItemCode());
				s.setBalLooseQuantity(stockUpdateRequest.getBalLooseQuantity());
				s.setBalPackQuantity(stockUpdateRequest.getPackQuantity());
				s.setBalQuantity(stockUpdateRequest.getPackQuantity() * stockUpdateRequest.getQtyPerBox()
						+ stockUpdateRequest.getBalLooseQuantity());
				s.setExpiryDate(stockUpdateRequest.getExpiryDate());
				s.setOnlineYesNo("Yes");
				s.setStoreId(stockUpdateRequest.getStoreId());
				s.setMrpPack(stockUpdateRequest.getMrpPack());
				if (stockUpdateRequest.getStockUpdateRequestType() == StockUpdateRequestType.PURCHASE) {
					s.setPurRatePerPackAfterGST(
							stockUpdateRequest.getTotalPurchaseValueAfterGST() / stockUpdateRequest.getQtyPerBox());
				}

				s.setStockValueMrp(stockUpdateRequest.getPackQuantity() * stockUpdateRequest.getMrpPack()
						+ stockUpdateRequest.getBalLooseQuantity()
								* (stockUpdateRequest.getMrpPack() / stockUpdateRequest.getQtyPerBox()));

				s.setStockValuePurrate(stockUpdateRequest.getPackQuantity() * s.getPurRatePerPackAfterGST()
						+ stockUpdateRequest.getBalLooseQuantity()
								* (s.getPurRatePerPackAfterGST() / stockUpdateRequest.getQtyPerBox()));
				s.setUpdatedBy(stockUpdateRequest.getUpdatedBy());
				s.setUpdatedAt(LocalDateTime.now());

				stockRepository.save(s);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public boolean checkStockAvailabilityTwo(OrderRequestDto orderDetails, String location, String itemName,
			String manufactureName) {
		if (location == null || location.isEmpty()) {
			throw new IllegalArgumentException("Location cannot be null or empty");
		}
		// Check if a store exists at the given location
		Optional<StoreEntity> store = storeRepository.findByLocationAndUserIdStoreId(location,
				orderDetails.getUserIdstoreId());
		if (store.isEmpty()) {
			throw new RuntimeException(
					"No store found at location: " + location + " and store ID: " + orderDetails.getUserIdstoreId());
		}
		// Get storeId from the StoreEntity
		// Check if the item is available in stock at the specified location (store)
		List<StockEntity> stockList = stockRepository
				.findAllByUserIdStoreIdAndItemNameAndMfName(orderDetails.getUserIdstoreId(), itemName, manufactureName);

		// If stock is available and quantity is sufficient, return true
		for (StockEntity stock : stockList) {
			// If stock is available and quantity is sufficient, return true
			if (stock.getBalQuantity() >= orderDetails.getOrderDetailsList().get(0).getOrderQty()) {
				return true;
			}
		}

		return false;
	}

	public void importDataInStock(MultipartFile reapExcelDataFile, String storeId, String emailId) throws Exception {
		Optional<StoreEntity> store = storeRepository.findById(storeId);
		if (store.isPresent()) {
			String ownerEmail = store.get().getOwnerEmail();
			if (!ownerEmail.equals(emailId)) {
				throw new APIException("User does not have access to upload the file");
			}
		} else {
			throw new APIException("Store not found for the given ID");
		}

		List<StockEntity> stockEntities = new ArrayList<>();
		try (XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream())) {
			XSSFSheet worksheet = workbook.getSheetAt(0);

			for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = worksheet.getRow(i);
				if (row == null || isRowEmpty(row)) { // Check if the row is blank
					continue;
				}

				StockEntity stockdata = new StockEntity();

				stockdata.setManufacturer(getStringValue(row, 0));
				stockdata.setMfName(getStringValue(row, 1));
				stockdata.setItemCode(getStringValue(row, 2));
				stockdata.setItemName(getStringValue(row, 3));
				stockdata.setSupplierName(getStringValue(row, 4));
				stockdata.setRack(getStringValue(row, 5));
				stockdata.setBatch(getStringValue(row, 6));
				stockdata.setExpiryDate(getDateValue(row, 7));
				stockdata.setBalQuantity(getDoubleValue(row, 8));
				stockdata.setBalPackQuantity(getDoubleValue(row, 9));
				stockdata.setBalLooseQuantity(getDoubleValue(row, 10));
				stockdata.setTotal(getStringValue(row, 11));
				stockdata.setMrpPack(getDoubleValue(row, 12));
				stockdata.setPurRatePerPackAfterGST(getDoubleValue(row, 13));
				stockdata.setMrpValue(getDoubleValue(row, 14));
				stockdata.setItemCategory(getStringValue(row, 15));
				stockdata.setOnlineYesNo(getStringValue(row, 16));
				stockdata.setStockValueMrp(getDoubleValue(row, 17));
				stockdata.setStockValuePurrate(getDoubleValue(row, 18));
				stockdata.setStoreId(storeId);
				stockdata.setUpdatedBy(emailId);
				stockdata.setUpdatedAt(LocalDateTime.now());
				stockEntities.add(stockdata);
			}
		}
		stockRepository.saveAll(stockEntities);
	}

	private boolean isRowEmpty(XSSFRow row) {
		for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
			XSSFCell cell = row.getCell(cellIndex);
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				String cellValue = cell.toString().trim();
				if (!cellValue.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	private String getStringValue(XSSFRow row, int cellIndex) {
		XSSFCell cell = row.getCell(cellIndex);
		return (cell != null) ? cell.toString().trim() : null;
	}

	private Double getDoubleValue(XSSFRow row, int cellIndex) {
		XSSFCell cell = row.getCell(cellIndex);
		return (cell != null && cell.getCellType() == CellType.NUMERIC) ? cell.getNumericCellValue() : null;
	}

	private Date getDateValue(XSSFRow row, int cellIndex) {
		XSSFCell cell = row.getCell(cellIndex);
		if (cell != null && cell.getCellType() == CellType.NUMERIC) {
			return cell.getDateCellValue();
		}
		return null;
	}

//	public List<StockEntity> saveStocksInvoices(List<StockRequest> reqs) {
//		List<StockEntity> purchases = maptoRequest(reqs);
//		stockRepository.saveAll(purchases);
//		return purchases;
//	}

	public List<StockEntity> saveStocksInvoices(List<StockRequest> reqs) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> registrationUser = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

		if (registrationUser.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to Store Users only.");
		}

		String userId = registrationUser.get().getStoreUserEmail(); // Get userId from RegistrationEntity

		List<StockEntity> purchases = maptoRequest(reqs, userId);
		stockRepository.saveAll(purchases);
		return purchases;
	}

	private List<StockEntity> maptoRequest(List<StockRequest> reqs, String userId) {
		return reqs.stream().map(request -> {
			StockEntity entity = new StockEntity();
			entity.setManufacturer(request.getManufacturer());
			entity.setMfName(request.getMfName());
			entity.setItemCode(request.getItemCode());
			entity.setItemName(request.getItemName());
			entity.setSupplierName(request.getSupplierName());
			entity.setRack(request.getRack());
			entity.setBatch(request.getBatch());
			entity.setExpiryDate(request.getExpiryDate());
			entity.setBalQuantity(request.getBalQuantity());
			entity.setBalPackQuantity(request.getBalPackQuantity());
			entity.setBalLooseQuantity(request.getBalLooseQuantity());
			entity.setTotal(request.getTotal());
			entity.setMrpPack(request.getMrpPack());
			entity.setPurRatePerPackAfterGST(request.getPurRatePerPackAfterGST());
			entity.setMrpValue(request.getMrpValue());
			entity.setItemCategory(request.getItemCategory());
			entity.setOnlineYesNo(request.getOnlineYesNo());
			entity.setStoreId(request.getStoreId());
			entity.setStockValueMrp(request.getStockValueMrp());
			entity.setStockValuePurrate(request.getStockValuePurrate());
			entity.setUpdatedBy(userId);
			entity.setUpdatedAt(LocalDateTime.now());
			return entity;
		}).toList();
	}

	public Map<String, Object> getStockReport(String itemName, String supplierName, String itemCategory,
			Date fromExpiryDate, Date toExpiryDate, String storeId, String userIdStoreId, String userId) {

		String loggedEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> travellerOpt = tabStoreRepository.findByStoreUserEmail(loggedEmail);

		if (travellerOpt.isEmpty()) {
			return Map.of("message", "Error! You don’t have permissions to access the resource", "status", false);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<StockEntity> query = cb.createQuery(StockEntity.class);
		Root<StockEntity> root = query.from(StockEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (itemName != null) {
			predicates.add(cb.equal(root.get("itemName"), itemName));
		}
		if (supplierName != null) {
			predicates.add(cb.equal(root.get("supplierName"), supplierName));
		}
		if (itemCategory != null) {
			predicates.add(cb.equal(root.get("itemCategory"), itemCategory));
		}
		if (fromExpiryDate != null && toExpiryDate != null) {
			predicates.add(cb.between(root.get("expiryDate"), fromExpiryDate, toExpiryDate));
		} else if (fromExpiryDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("expiryDate"), fromExpiryDate));
		} else if (toExpiryDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("expiryDate"), toExpiryDate));
		}
		if (storeId != null) {
			predicates.add(cb.equal(root.get("storeId"), storeId));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		if (userId != null) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}

		query.where(predicates.toArray(new Predicate[0]));
		List<StockEntity> stockList = entityManager.createQuery(query).getResultList();

		// Fetch all purchase records with matching itemCodes
		List<String> itemCodes = stockList.stream().map(StockEntity::getItemCode).distinct()
				.collect(Collectors.toList());

		CriteriaQuery<PurchaseEntity> purchaseQuery = cb.createQuery(PurchaseEntity.class);
		Root<PurchaseEntity> purchaseRoot = purchaseQuery.from(PurchaseEntity.class);
		purchaseQuery.where(purchaseRoot.get("itemCode").in(itemCodes));
		List<PurchaseEntity> purchaseList = entityManager.createQuery(purchaseQuery).getResultList();

		List<String> itemOffers = stockList.stream().map(StockEntity::getUserIdStoreIdItemCode).distinct()
				.collect(Collectors.toList());
		CriteriaQuery<ItemOfferEntity> itemQuery = cb.createQuery(ItemOfferEntity.class);
		Root<ItemOfferEntity> itemRoot = itemQuery.from(ItemOfferEntity.class);
		itemQuery.where(itemRoot.get("userIdStoreIdItemCode").in(itemOffers));
		List<ItemOfferEntity> itemOfferList = entityManager.createQuery(itemQuery).getResultList();
		// Map itemCode -> gstCode
		Map<String, Double> itemCodeToIgstPer = purchaseList.stream()
				.collect(Collectors.toMap(PurchaseEntity::getItemCode, PurchaseEntity::getIGSTPer, (a, b) -> a)); // Keep
																													// first

		Map<String, ItemOfferEntity> itemOfferMap = itemOfferList.stream().collect(
				Collectors.toMap(i -> i.getUserIdStoreIdItemCode() + "_" + i.getBatchNumber(), i -> i, (a, b) -> a // if
																													// duplicates,
																													// keep
																													// the
																													// first
				));

		List<StockReportDTOEn> reportList = stockList.stream().map(stock -> {
			Double igstCode = itemCodeToIgstPer.getOrDefault(stock.getItemCode(), (double) 0);
			String offerKey = stock.getUserIdStoreIdItemCode() + "_" + stock.getBatch();
			ItemOfferEntity offer = itemOfferMap.get(offerKey);

			Double minOrdQty = offer != null ? offer.getMinOrderQty() : 0.0;
			Double offerQty = offer != null ? offer.getOfferQty() : 0.0;
			Double discount = offer != null ? offer.getDiscount() : 0.0;

			return new StockReportDTOEn(stock.getManufacturer(), stock.getMfName(), stock.getItemCode(),
					stock.getItemName(), stock.getSupplierName(), stock.getRack(), stock.getBatch(),
					stock.getExpiryDate(), stock.getBalQuantity(), stock.getBalPackQuantity(),
					stock.getBalLooseQuantity(), stock.getTotal(), stock.getMrpPack(),
					stock.getPurRatePerPackAfterGST(), stock.getMrpValue(), stock.getItemCategory(),
					stock.getOnlineYesNo(), stock.getStoreId(), stock.getStockValueMrp(), stock.getStockValuePurrate(),
					stock.getUpdatedBy(), stock.getUpdatedAt(), stock.getUserId(), stock.getUserIdStoreIdItemCode(),
					stock.getUserIdStoreId(), igstCode, minOrdQty, offerQty, discount);
		}).collect(Collectors.toList());

		return Map.of("message", "Stock report fetched successfully", "status", true, "data", reportList);
	}

	public Map<String, Object> getstockbyItemCode(String itemCode, String userIdStoreId, String userId) {
		String loggedEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> travellerOpt = tabStoreRepository.findByStoreUserEmail(loggedEmail);

		if (travellerOpt.isEmpty()) {
			return Map.of("message", "Error! You don’t have permissions to access the resource", "status", false);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<StockEntity> query = cb.createQuery(StockEntity.class);
		Root<StockEntity> root = query.from(StockEntity.class);
		List<Predicate> predicates = new ArrayList<>();
		if (itemCode != null) {
			predicates.add(cb.equal(root.get("itemCode"), itemCode));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		if (userId != null) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		query.where(predicates.toArray(new Predicate[0]));
		List<StockEntity> stockList = entityManager.createQuery(query).getResultList();

		List<String> itemCodes = stockList.stream().map(StockEntity::getItemCode).distinct()
				.collect(Collectors.toList());

		CriteriaQuery<PurchaseEntity> purchaseQuery = cb.createQuery(PurchaseEntity.class);
		Root<PurchaseEntity> purchaseRoot = purchaseQuery.from(PurchaseEntity.class);
		purchaseQuery.where(purchaseRoot.get("itemCode").in(itemCodes));
		List<PurchaseEntity> purchaseList = entityManager.createQuery(purchaseQuery).getResultList();

		// Map itemCode -> gstCode
		Map<String, Integer> itemCodeToGst = purchaseList.stream()
				.collect(Collectors.toMap(PurchaseEntity::getItemCode, PurchaseEntity::getGstCode, (a, b) -> a)); // Keep
																													// first

		// Create response DTO list
		List<StockReportDTO> reportList = stockList.stream().map(stock -> {
			Integer gstCode = itemCodeToGst.getOrDefault(stock.getItemCode(), null);
			return new StockReportDTO(stock.getManufacturer(), stock.getMfName(), stock.getItemCode(),
					stock.getItemName(), // You missed this
					stock.getSupplierName(), stock.getRack(), stock.getBatch(), stock.getExpiryDate(),
					stock.getBalQuantity(), stock.getBalPackQuantity(), stock.getBalLooseQuantity(), stock.getTotal(),
					stock.getMrpPack(), stock.getPurRatePerPackAfterGST(), stock.getMrpValue(), stock.getItemCategory(),
					stock.getOnlineYesNo(), stock.getStoreId(), stock.getStockValueMrp(), stock.getStockValuePurrate(),
					stock.getUpdatedBy(), stock.getUpdatedAt(), stock.getUserId(), stock.getUserIdStoreIdItemCode(),
					stock.getUserIdStoreId(), gstCode);
		}).collect(Collectors.toList());

		return Map.of("message", "Stock report fetched successfully", "status", true, "data", reportList);

	}

	public Map<String, Object> getStockByItemName(String itemName) {
		String loggedEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> travellerOpt = tabStoreRepository.findByStoreUserEmail(loggedEmail);

		if (travellerOpt.isEmpty()) {
			return Map.of("message", "Error! You don’t have permissions to access the resource", "status", false);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<StockEntity> root = query.from(StockEntity.class);

		List<Predicate> predicates = new ArrayList<>();
		if (itemName != null && !itemName.isBlank()) {
			predicates.add(cb.equal(root.get("itemName"), itemName));
		}

		query.multiselect(root.get("itemCode"), root.get("manufacturer"), root.get("mfName"));

		query.where(cb.and(predicates.toArray(new Predicate[0])));
		List<Object[]> resultList = entityManager.createQuery(query).getResultList();

		List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
			Map<String, Object> map = new HashMap<>();
			map.put("itemCode", obj[0]);
			map.put("manufacturer", obj[1]);
			map.put("mfName", obj[2]);
			return map;
		}).collect(Collectors.toList());

		return Map.of("message", "Stock report fetched successfully", "status", true, "data", responseData);
	}

	public Map<String, Object> getStockByItemNameEnhanced(String itemName) {
		String loggedEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> travellerOpt = tabStoreRepository.findByStoreUserEmail(loggedEmail);

		if (travellerOpt.isEmpty()) {
			return Map.of("message", "Error! You don’t have permissions to access the resource", "status", false);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

		Root<StockEntity> stockRoot = query.from(StockEntity.class);
		Root<ItemCodeMaster> itemCodeRoot = query.from(ItemCodeMaster.class);
		Root<PurchaseEntity> purchaseRoot = query.from(PurchaseEntity.class);

		List<Predicate> predicates = new ArrayList<>();

		// Join condition on userIdStoreIdItemCode
		predicates.add(cb.equal(stockRoot.get("userIdStoreIdItemCode"), itemCodeRoot.get("userIdStoreIdItemCode")));
		predicates.add(cb.equal(stockRoot.get("userIdStoreId"), purchaseRoot.get("userIdStoreId")));

		if (itemName != null && !itemName.isBlank()) {
			predicates.add(cb.equal(stockRoot.get("itemName"), itemName));
		}

		query.multiselect(stockRoot.get("itemCode"), //
				stockRoot.get("mfName"), stockRoot.get("batch"), //
				stockRoot.get("expiryDate"), //
				itemCodeRoot.get("brand"), // brand from ItemCodeMaster
				itemCodeRoot.get("itemName"), //
				itemCodeRoot.get("itemCategory"), itemCodeRoot.get("itemSubCategory"), itemCodeRoot.get("gst"),
				itemCodeRoot.get("hsnGroup"), itemCodeRoot.get("manufacturer"), //
				purchaseRoot.get("mRP"), purchaseRoot.get("purRate"));

		query.where(cb.and(predicates.toArray(new Predicate[0])));
		List<Object[]> resultList = entityManager.createQuery(query).getResultList();

		List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
			Map<String, Object> map = new HashMap<>();
			map.put("itemCode", obj[0]);
			map.put("mfName", obj[1]);
			map.put("batch", obj[2]);
			map.put("expiryDate", obj[3]);
			map.put("brand", obj[4]); // include brand
			map.put("itemName", obj[5]);
			map.put("itemCategory", obj[6]);
			map.put("itemSubCategory", obj[7]);
			map.put("gst", obj[8]);
			map.put("hsnGroup", obj[9]);
			map.put("manufacturer", obj[10]);
			map.put("mRP", obj[11]);
			map.put("purRate", obj[12]);
			return map;
		}).collect(Collectors.toList());

		return Map.of("message", "Stock report fetched successfully", "status", true, "data", responseData);
	}

	public List<String> getItemName() {
		return stockRepository.findItemName();
	}

	public List<StockEntity> getStocksByStoreId(String storeId) {
		return stockRepository.findByStoreIdOne(storeId);
	}

	public List<StockEntity> saveStocksInvoicesEn(List<StockRequest> reqs) {
		Optional<StoreEntity> storeEntityOptional = storeRepository.findById(reqs.get(0).getStoreId());

		// Check if the store exists and is active
		if (storeEntityOptional.isEmpty() || !"active".equalsIgnoreCase(storeEntityOptional.get().getStatus())) {
			throw new ResourceNotFoundException("Store is either not found or not active.");
		}
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> registrationUser = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

		if (registrationUser.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to Store Users only.");
		}

		String userId = registrationUser.get().getStoreUserEmail(); // Get userId from RegistrationEntity

		List<StockEntity> purchases = maptoRequestEn(reqs, userId);
		stockRepository.saveAll(purchases);
		return purchases;
	}

	private List<StockEntity> maptoRequestEn(List<StockRequest> reqs, String userId) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> login = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		return reqs.stream().map(request -> {
			StockEntity entity = new StockEntity();
			entity.setManufacturer(request.getManufacturer());
			entity.setMfName(request.getMfName());
			entity.setItemCode(request.getItemCode());
			entity.setItemName(request.getItemName());
			entity.setSupplierName(request.getSupplierName());
			entity.setRack(request.getRack());
			entity.setBatch(request.getBatch());
			entity.setExpiryDate(request.getExpiryDate());
			entity.setBalQuantity(request.getBalQuantity());
			entity.setBalPackQuantity(request.getBalPackQuantity());
			entity.setBalLooseQuantity(request.getBalLooseQuantity());
			entity.setTotal(request.getTotal());
			entity.setMrpPack(request.getMrpPack());
			entity.setPurRatePerPackAfterGST(request.getPurRatePerPackAfterGST());
			entity.setMrpValue(request.getMrpValue());
			entity.setItemCategory(request.getItemCategory());
			entity.setOnlineYesNo(request.getOnlineYesNo());
			entity.setStoreId(request.getStoreId());
			entity.setStockValueMrp(request.getStockValueMrp());
			entity.setStockValuePurrate(request.getStockValuePurrate());
			entity.setUpdatedBy(login.get().getUserId());
			entity.setUpdatedAt(LocalDateTime.now());
			entity.setUserId(login.get().getUserId());
			entity.setUserIdStoreIdItemCode(
					login.get().getUserId() + "_" + request.getStoreId() + "_" + request.getItemCode());
			entity.setUserIdStoreId(login.get().getUserId() + "_" + request.getStoreId());
			return entity;
		}).toList();
	}

	public Map<String, Object> getStockByItemNames(String itemName) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
		Root<StockEntity> root = query.from(StockEntity.class);

		List<Predicate> predicates = new ArrayList<>();
		if (itemName != null && !itemName.isBlank()) {
			predicates.add(cb.like(cb.lower(root.get("itemName")), "%" + itemName.toLowerCase() + "%"));
		}

		query.multiselect(root.get("itemName"));
		query.where(cb.and(predicates.toArray(new Predicate[0])));

		List<Object[]> resultList = entityManager.createQuery(query).getResultList();

		List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
			Map<String, Object> map = new HashMap<>();
			map.put("itemName", obj[0]);
			return map;
		}).collect(Collectors.toList());

		return Map.of("message", "itemName report fetched successfully", "status", true, "totalCount",
				responseData.size(), // ✅ Total count added
				"data", responseData);
	}

	public String updateMrpByUserIdStoreIdItemCode(StockMrpUpdateRequest request) {
		StockEntity stock = stockRepository.findByUserIdStoreIdItemCodeAndBatch(request.getUserIdStoreIdItemCode(),request.getBatch());

		if (stock != null) {
			stock.setMrpPack(request.getMrpPack());
			stock.setMrpValue(request.getMrpValue());
			stock.setUpdatedAt(LocalDateTime.now());
			stockRepository.save(stock);
			return "MRP updated successfully.";
		} else {
			return "Stock not found for userIdStoreIdItemCode: " + request.getUserIdStoreIdItemCode();
		}
	}

}