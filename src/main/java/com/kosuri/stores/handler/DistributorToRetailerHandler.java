package com.kosuri.stores.handler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.DistributorRetailerOrderDetailsEntity;
import com.kosuri.stores.dao.DistributorRetailerOrderDetailsRepo;
import com.kosuri.stores.dao.DistributorRetailerOrderHdrEntity;
import com.kosuri.stores.dao.DistributorRetailerOrderHdrRepo;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.DistributorRetailerOrderDetailsDto;
import com.kosuri.stores.model.dto.DistributorRetailerOrderHdrEnrichedDto;
import com.kosuri.stores.model.dto.DistributorRetailerOrderResponseDTO;
import com.kosuri.stores.model.dto.RetailerOrderRequestDto;
import com.kosuri.stores.model.dto.TabStoreUserInfoDto;
import com.kosuri.stores.model.dto.UpdateRetailerOrderRequestDto;
import com.kosuri.stores.model.enums.OrderStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DistributorToRetailerHandler {

	private TabStoreUserInfoDto retailerInfo;
	private TabStoreUserInfoDto distributorInfo;

	@Autowired
	private DistributorRetailerOrderHdrRepo orderHdrRepo;

	@Autowired
	private DistributorRetailerOrderDetailsRepo orderDetailsRepo;

	@Autowired
	private StockHandler stockHandler;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ModelMapper modelMapper;

	public DistributorRetailerOrderHdrEntity placeOrder(RetailerOrderRequestDto dto) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		Optional<StoreEntity> store = storeRepository.findById(dto.getStoreId());
		// Create order header
		DistributorRetailerOrderHdrEntity orderHdr = new DistributorRetailerOrderHdrEntity();
		orderHdr.setStatus(OrderStatus.PENDING);
		orderHdr.setRetailerId(dto.getRetailerId());
		orderHdr.setDistrubutorId(dto.getDistrubutorId());
		orderHdr.setStoreId(store.get().getId());
		orderHdr.setOrderDate(new Date());
		orderHdr.setInvoiceNo(dto.getInvoiceNo());
		String updatedBy;
		if (loginStore.isPresent()) {
			updatedBy = loginStore.get().getUserId();

		} else {
			updatedBy = login.get().getCId(); // Customer IDUser ID

		}
		orderHdr.setUpdatedBy(updatedBy);
		orderHdr.setRetailerId(updatedBy);
		orderHdrRepo.save(orderHdr);

		// Create and save order details
		List<DistributorRetailerOrderDetailsDto> orderDetailsList = dto.getOrderDetailsList();
		String baseOrderId = orderHdr.getOrderId(); // e.g., OR2025846584
		int counter = 1;
		List<DistributorRetailerOrderDetailsEntity> detailEntities = new ArrayList<>();
		for (DistributorRetailerOrderDetailsDto orderDetails : orderDetailsList) {
			DistributorRetailerOrderDetailsEntity orderDetailsEntity = new DistributorRetailerOrderDetailsEntity();
			orderDetailsEntity.setRetailerId(orderDetails.getRetailerId());
			orderDetailsEntity.setItemName(orderDetails.getItemName());
			StockEntity stockEntity = stockRepository.findByItemCode(orderDetails.getItemCode());
			if (stockEntity != null) {
				orderDetailsEntity.setItemCode(stockEntity.getItemCode());
			}
			orderDetailsEntity.setItemCategory(orderDetails.getItemCategory());
			orderDetailsEntity.setBrandName(orderDetails.getBrandName());
			orderDetailsEntity.setManufacturerName(orderDetails.getManufacturerName());
			orderDetailsEntity.setOrderQuantity(orderDetails.getOrderQuantity());
			orderDetailsEntity.setDeliveryQuantity(orderDetails.getDeliveryQuantity());
			orderDetailsEntity.setMrp(orderDetails.getMrp());
			orderDetailsEntity.setDiscount(orderDetails.getDiscount());
			orderDetailsEntity.setCashDiscount(orderDetails.getCashDiscount());
			orderDetailsEntity.setOffer(orderDetails.getOffer());
			orderDetailsEntity.setSgst(orderDetails.getSgst());
			orderDetailsEntity.setCgst(orderDetails.getCgst());
			orderDetailsEntity.setSgstAmount(orderDetails.getSgstAmount());
			orderDetailsEntity.setCgstAmount(orderDetails.getCgstAmount());
			orderDetailsEntity.setTotal(orderDetails.getTotal());
			orderDetailsEntity.setStatus(orderDetails.getStatus());
			orderDetailsEntity.setDistributorId(orderDetails.getDistributorId());
			orderDetailsEntity.setBatchNumber(orderDetails.getBatchNumber());
			String orderLineId = baseOrderId + "_" + String.format("%03d", counter++);
			orderDetailsEntity.setOrderlineId(orderLineId);
			orderDetailsEntity.setUserIdStoreIdItemCode(stockEntity.getUserIdStoreIdItemCode());
			orderDetailsEntity.setExpiryDate(orderDetails.getExpiryDate());
			orderDetailsEntity.setStoreId(dto.getStoreId());
			orderDetailsEntity.setInvoiceNo(dto.getInvoiceNo());
			orderDetailsEntity.setDistributorRetailerOrderHdr(orderHdr);
			detailEntities.add(orderDetailsEntity);
			orderDetailsRepo.save(orderDetailsEntity);
		}

		return orderHdr;
	}

	public List<DistributorRetailerOrderDetailsEntity> updateOrderDetails(UpdateRetailerOrderRequestDto dto) {
		String orderId = dto.getOrderId();
		List<DistributorRetailerOrderDetailsEntity> updatedEntities = new ArrayList<>();

		for (DistributorRetailerOrderDetailsDto details : dto.getOrderDetailsList()) {
			String orderlineId = details.getOrderlineId();

			if (orderlineId == null || !orderlineId.contains("_")) {
				throw new RuntimeException("Invalid orderlineId format: " + orderlineId);
			}

			String prefix = orderlineId.split("_")[0];
			if (!prefix.equals(orderId)) {
				throw new RuntimeException(
						"Mismatch: orderlineId '" + orderlineId + "' does not belong to orderId '" + orderId + "'");
			}

			DistributorRetailerOrderDetailsEntity entity = orderDetailsRepo.findByOrderlineId(orderlineId)
					.orElseThrow(() -> new RuntimeException("OrderLineId not found: " + orderlineId));

			// Update fields
			entity.setDeliveryQuantity(details.getDeliveryQuantity());
			entity.setTotal(details.getTotal());
			entity.setBatchNumber(details.getBatchNumber());
			entity.setExpiryDate(details.getExpiryDate());

			DistributorRetailerOrderDetailsEntity saved = orderDetailsRepo.save(entity);
			updatedEntities.add(saved);

			// Convert LocalDate to Date
			LocalDate localDate = entity.getExpiryDate();
			Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			// ----- Update SaleEntity -----
			SaleEntity saleOrder = saleRepository.findByUserIdStoreIdItemCodeOne(entity.getUserIdStoreIdItemCode());
			if (saleOrder != null) {
				saleOrder.setOrderId(saved.getDistributorRetailerOrderHdr().getOrderId());
				saleOrder.setSaleType(details.getSaleType());
				saleOrder.setBatchNo(entity.getBatchNumber());
				saleOrder.setExpiryDate(date);
				saleOrder.setTotal(entity.getTotal());
				saleOrder.setQty(details.getDeliveryQuantity().doubleValue());

				saleRepository.save(saleOrder);
			}

			// ----- Update StockEntity -----
//	        StockEntity stockOrder = stockRepository.findByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());
//	        if (stockOrder != null) {
//	            stockOrder.setBatch(entity.getBatchNumber());
//	            stockOrder.setExpiryDate(date);
//	            stockOrder.setTotal(Double.toString(entity.getTotal()));
//	            stockOrder.setBalQuantity(stockOrder.getBalQuantity() - entity.getDeliveryQuantity().doubleValue());
//
//	            stockRepository.save(stockOrder);
//	        }
			StockEntity stockOrder = stockRepository.findByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());

			if (stockOrder == null) {
				throw new RuntimeException("No existing stock record found for: " + entity.getUserIdStoreIdItemCode());
			}

			stockOrder.setBatch(entity.getBatchNumber());
			stockOrder.setExpiryDate(date);
			stockOrder.setTotal(Double.toString(entity.getTotal()));
			stockOrder.setBalQuantity(stockOrder.getBalQuantity() - entity.getDeliveryQuantity().doubleValue());

			stockRepository.save(stockOrder);

		}

		return updatedEntities;
	}

	public Page<DistributorRetailerOrderDetailsEntity> getDistributorRetailerOrderDetails(String orderlineId,
			String retailerId, String itemName, String itemCategory, String brandName, String manufacturerName,
			String distributorId, String itemCode, String storeId, String userIdStoreIdItemCode, String invoiceNo,
			String fromDate, String toDate, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// === Main query ===
		CriteriaQuery<DistributorRetailerOrderDetailsEntity> query = cb
				.createQuery(DistributorRetailerOrderDetailsEntity.class);
		Root<DistributorRetailerOrderDetailsEntity> root = query.from(DistributorRetailerOrderDetailsEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (orderlineId != null && !orderlineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("orderlineId"), orderlineId));
		}
		if (retailerId != null && !retailerId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("retailerId"), retailerId));
		}
		if (itemName != null && !itemName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemName"), itemName));
		}
		if (itemCategory != null && !itemCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemCategory"), itemCategory));
		}
		if (brandName != null && !brandName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brandName"), brandName));
		}
		if (manufacturerName != null && !manufacturerName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("manufacturerName"), manufacturerName));
		}
		if (distributorId != null && !distributorId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("distributorId"), distributorId));
		}
		if (itemCode != null && !itemCode.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemCode"), itemCode));
		}
		if (storeId != null && !storeId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("storeId"), storeId));
		}
		if (userIdStoreIdItemCode != null && !userIdStoreIdItemCode.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));
		}
		if (invoiceNo != null && !invoiceNo.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("invoiceNo"), invoiceNo));
		}

		// Join with DistributorRetailerOrderHdrEntity to access orderDate
		Join<DistributorRetailerOrderDetailsEntity, DistributorRetailerOrderHdrEntity> hdrJoin = root
				.join("distributorRetailerOrderHdr", JoinType.INNER);

		// Apply filters on orderDate from the joined header entity
		if (fromDate != null && toDate != null) {
			predicates.add(cb.between(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate),
					java.sql.Date.valueOf(toDate)));
		} else if (fromDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate)));
		} else if (toDate != null) {
			predicates.add(cb.lessThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(toDate)));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<DistributorRetailerOrderDetailsEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// === Count query ===
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<DistributorRetailerOrderDetailsEntity> countRoot = countQuery
				.from(DistributorRetailerOrderDetailsEntity.class);
		Join<DistributorRetailerOrderDetailsEntity, DistributorRetailerOrderHdrEntity> countHdrJoin = countRoot
				.join("distributorRetailerOrderHdr", JoinType.INNER);
		List<Predicate> countPredicates = new ArrayList<>();

		if (orderlineId != null && !orderlineId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("orderlineId"), orderlineId));
		}
		if (retailerId != null && !retailerId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("retailerId"), retailerId));
		}
		if (itemName != null && !itemName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemName"), itemName));
		}
		if (itemCategory != null && !itemCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemCategory"), itemCategory));
		}
		if (brandName != null && !brandName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("brandName"), brandName));
		}
		if (manufacturerName != null && !manufacturerName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("manufacturerName"), manufacturerName));
		}
		if (distributorId != null && !distributorId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("distributorId"), distributorId));
		}
		if (itemCode != null && !itemCode.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("itemCode"), itemCode));
		}
		if (storeId != null && !storeId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("storeId"), storeId));
		}
		if (userIdStoreIdItemCode != null && !userIdStoreIdItemCode.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));
		}
		if (invoiceNo != null && !invoiceNo.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("invoiceNo"), invoiceNo));
		}
		if (fromDate != null && toDate != null) {
			countPredicates.add(cb.between(countHdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate),
					java.sql.Date.valueOf(toDate)));
		} else if (fromDate != null) {
			countPredicates
					.add(cb.greaterThanOrEqualTo(countHdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate)));
		} else if (toDate != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countHdrJoin.get("orderDate"), java.sql.Date.valueOf(toDate)));
		}
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Transactional(readOnly = true)
	public ApiResponse<DistributorRetailerOrderResponseDTO> getDistributorRetailerOrderDetail(String orderid,
			String orderlineId, String retailerId, String itemName, String itemCategory, String brandName,
			String manufacturerName, String distributorId, String itemCode, String storeId,
			String userIdStoreIdItemCode, String invoiceNo, String fromDate, String toDate, Pageable pageable) {

		Page<DistributorRetailerOrderDetailsEntity> page = fetchFilteredOrderDetails(orderid, orderlineId, retailerId,
				itemName, itemCategory, brandName, manufacturerName, distributorId, itemCode, storeId,
				userIdStoreIdItemCode, invoiceNo, fromDate, toDate, pageable);

		if (page.isEmpty()) {
			return new ApiResponse<>("No records found", false, null);
		}

		DistributorRetailerOrderDetailsEntity first = page.getContent().get(0);
		String orderId = first.getDistributorRetailerOrderHdr().getOrderId();

		DistributorRetailerOrderResponseDTO responseDTO = new DistributorRetailerOrderResponseDTO();
		responseDTO.setOrderId(orderId);
		responseDTO.setRetailerId(first.getDistributorRetailerOrderHdr().getRetailerId());
		responseDTO.setStoreId(first.getStoreId());
		responseDTO.setDistributorId(first.getDistributorRetailerOrderHdr().getDistrubutorId());
		responseDTO.setInvoiceNo(first.getInvoiceNo());

		// ✅ Filter list where orderlineId starts with orderId
		List<DistributorRetailerOrderDetailsDto> filteredDtoList = page.getContent().stream().filter(entity -> {
			String orderline = entity.getOrderlineId();
			return orderline != null && orderline.startsWith(orderId + "_");
		}).map(entity -> modelMapper.map(entity, DistributorRetailerOrderDetailsDto.class))
				.collect(Collectors.toList());

		responseDTO.setOrderDetailsList(filteredDtoList);
		responseDTO.setCurrentPage(page.getNumber());
		responseDTO.setPageSize(page.getSize());
		responseDTO.setTotalElements(filteredDtoList.size());
		responseDTO.setTotalPages(1); // since we filtered, the totalPages should be adjusted accordingly

		return new ApiResponse<>("Distributor Retailer Order details retrieved successfully.", true, responseDTO);
	}

	private Page<DistributorRetailerOrderDetailsEntity> fetchFilteredOrderDetails(String orderid, String orderlineId,
			String retailerId, String itemName, String itemCategory, String brandName, String manufacturerName,
			String distributorId, String itemCode, String storeId, String userIdStoreIdItemCode, String invoiceNo,
			String fromDate, String toDate, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DistributorRetailerOrderDetailsEntity> cq = cb
				.createQuery(DistributorRetailerOrderDetailsEntity.class);
		Root<DistributorRetailerOrderDetailsEntity> root = cq.from(DistributorRetailerOrderDetailsEntity.class);
		Join<DistributorRetailerOrderDetailsEntity, DistributorRetailerOrderHdrEntity> hdrJoin = root
				.join("distributorRetailerOrderHdr", JoinType.INNER);

		List<Predicate> predicates = new ArrayList<>();

		if (orderid != null && !orderid.trim().isEmpty()) {
			predicates.add(cb.equal(hdrJoin.get("orderId"), orderid));
		}
		if (orderlineId != null && !orderlineId.trim().isEmpty())
			predicates.add(cb.equal(root.get("orderlineId"), orderlineId));

		if (retailerId != null && !retailerId.trim().isEmpty())
			predicates.add(cb.equal(root.get("retailerId"), retailerId));

		if (itemName != null && !itemName.trim().isEmpty())
			predicates.add(cb.equal(root.get("itemName"), itemName));

		if (itemCategory != null && !itemCategory.trim().isEmpty())
			predicates.add(cb.equal(root.get("itemCategory"), itemCategory));

		if (brandName != null && !brandName.trim().isEmpty())
			predicates.add(cb.equal(root.get("brandName"), brandName));

		if (manufacturerName != null && !manufacturerName.trim().isEmpty())
			predicates.add(cb.equal(root.get("manufacturerName"), manufacturerName));

		if (distributorId != null && !distributorId.trim().isEmpty())
			predicates.add(cb.equal(root.get("distributorId"), distributorId));

		if (itemCode != null && !itemCode.trim().isEmpty())
			predicates.add(cb.equal(root.get("itemCode"), itemCode));

		if (storeId != null && !storeId.trim().isEmpty())
			predicates.add(cb.equal(root.get("storeId"), storeId));

		if (userIdStoreIdItemCode != null && !userIdStoreIdItemCode.trim().isEmpty())
			predicates.add(cb.equal(root.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));

		if (invoiceNo != null && !invoiceNo.trim().isEmpty())
			predicates.add(cb.equal(root.get("invoiceNo"), invoiceNo));

		if (fromDate != null && toDate != null) {
			predicates.add(cb.between(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate),
					java.sql.Date.valueOf(toDate)));
		} else if (fromDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate)));
		} else if (toDate != null) {
			predicates.add(cb.lessThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(toDate)));
		}

		cq.where(predicates.toArray(new Predicate[0]));
		cq.orderBy(cb.desc(root.get("orderlineId")));

		TypedQuery<DistributorRetailerOrderDetailsEntity> query = entityManager.createQuery(cq);

		int totalRows = query.getResultList().size();

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<DistributorRetailerOrderDetailsEntity> resultList = query.getResultList();

		return new PageImpl<>(resultList, pageable, totalRows);
	}

//	@Transactional(readOnly = true)
//	public Page<DistributorRetailerOrderHdrEntity> getDistributorRetailerOrderHdr(String orderId, String retailerId,
//			String distrubutorId, String storeId, String invoiceNo,String fromDate,String toDate, Pageable pageable) {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//		// === Selection query ===
//		CriteriaQuery<DistributorRetailerOrderHdrEntity> query = cb
//				.createQuery(DistributorRetailerOrderHdrEntity.class);
//		Root<DistributorRetailerOrderHdrEntity> root = query.from(DistributorRetailerOrderHdrEntity.class);
//		List<Predicate> predicates = new ArrayList<>();
//
//		if (orderId != null && !orderId.trim().isEmpty()) {
//			predicates.add(cb.equal(root.get("orderId"), orderId));
//		}
//		if (retailerId != null && !retailerId.trim().isEmpty()) {
//			predicates.add(cb.equal(root.get("retailerId"), retailerId));
//		}
//		if (distrubutorId != null && !distrubutorId.trim().isEmpty()) {
//			predicates.add(cb.equal(root.get("distrubutorId"), distrubutorId));
//		}
//		if (storeId != null && !storeId.trim().isEmpty()) {
//			predicates.add(cb.equal(root.get("storeId"), storeId));
//		}
//		if (invoiceNo != null && !invoiceNo.trim().isEmpty()) {
//			predicates.add(cb.equal(root.get("invoiceNo"), invoiceNo));
//		}
//		if (fromDate != null && toDate!= null) {
//			predicates.add(cb.between(root.get("orderDate"), fromDate, toDate));
//		} else if (toDate != null) {
//			predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate));
//		} else if (toDate != null) {
//			predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), toDate));
//		}
//		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//		TypedQuery<DistributorRetailerOrderHdrEntity> typedQuery = entityManager.createQuery(query);
//		typedQuery.setFirstResult((int) pageable.getOffset());
//		typedQuery.setMaxResults(pageable.getPageSize());
//
//		// === Count query ===
//		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//		Root<DistributorRetailerOrderHdrEntity> countRoot = countQuery.from(DistributorRetailerOrderHdrEntity.class);
//		List<Predicate> countPredicates = new ArrayList<>();
//
//		if (orderId != null && !orderId.trim().isEmpty()) {
//			countPredicates.add(cb.equal(countRoot.get("orderId"), orderId));
//		}
//		if (retailerId != null && !retailerId.trim().isEmpty()) {
//			countPredicates.add(cb.equal(countRoot.get("retailerId"), retailerId));
//		}
//		if (distrubutorId != null && !distrubutorId.trim().isEmpty()) {
//			countPredicates.add(cb.equal(countRoot.get("distrubutorId"), distrubutorId));
//		}
//		if (storeId != null && !storeId.trim().isEmpty()) {
//			countPredicates.add(cb.equal(countRoot.get("storeId"), storeId));
//		}
//		if (invoiceNo != null && !invoiceNo.trim().isEmpty()) {
//			countPredicates.add(cb.equal(countRoot.get("invoiceNo"), invoiceNo));
//		}
//
//		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
//		Long total = entityManager.createQuery(countQuery).getSingleResult();
//
//		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
//	}
	public Page<DistributorRetailerOrderHdrEnrichedDto> getDistributorRetailerOrderHdr(
	        String orderId, String retailerId, String distrubutorId,
	        String storeId, String invoiceNo, String fromDate, String toDate, Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    CriteriaQuery<DistributorRetailerOrderHdrEntity> query = cb.createQuery(DistributorRetailerOrderHdrEntity.class);
	    Root<DistributorRetailerOrderHdrEntity> root = query.from(DistributorRetailerOrderHdrEntity.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (orderId != null && !orderId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("orderId"), orderId));
	    }
	    if (retailerId != null && !retailerId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("retailerId"), retailerId));
	    }
	    if (distrubutorId != null && !distrubutorId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("distrubutorId"), distrubutorId));
	    }
	    if (storeId != null && !storeId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("storeId"), storeId));
	    }
	    if (invoiceNo != null && !invoiceNo.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("invoiceNo"), invoiceNo));
	    }
	    if (fromDate != null && toDate != null) {
	        predicates.add(cb.between(root.get("orderDate"), fromDate, toDate));
	    } else if (fromDate != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), fromDate));
	    } else if (toDate != null) {
	        predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), toDate));
	    }

	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    TypedQuery<DistributorRetailerOrderHdrEntity> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    List<DistributorRetailerOrderHdrEntity> resultList = typedQuery.getResultList();

	    // Fetch enriched DTOs
	    List<DistributorRetailerOrderHdrEnrichedDto> enrichedList = resultList.stream().map(order -> {
	        DistributorRetailerOrderHdrEnrichedDto dto = new DistributorRetailerOrderHdrEnrichedDto();
	        dto.setOrderId(order.getOrderId());
	        dto.setOrderDate(order.getOrderDate());
	        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
	        dto.setOrderUpdatedDate(order.getOrderUpdatedDate());
	        dto.setOrderUpdatedBy(order.getOrderUpdatedBy());
	        dto.setUpdatedBy(order.getUpdatedBy());
	        dto.setRetailerId(order.getRetailerId());
	        dto.setDistrubutorId(order.getDistrubutorId());
	        dto.setStoreId(order.getStoreId());
	        dto.setInvoiceNo(order.getInvoiceNo());

	        tabStoreRepository.findById(order.getRetailerId()).ifPresent(retailer -> {
	        	TabStoreUserInfoDto rInfo = new TabStoreUserInfoDto(
	        			retailer.getUserId(),
	                    retailer.getType(),
	                    retailer.getUsername(),
	                    retailer.getStoreUserContact(),
	                    retailer.getStoreUserEmail());
	            dto.setRetailerInfo(rInfo);
	        });

	        tabStoreRepository.findById(order.getDistrubutorId()).ifPresent(distributor -> {
	        	TabStoreUserInfoDto dInfo = new TabStoreUserInfoDto(
	        			distributor.getUserId(),
	                    distributor.getType(),
	                    distributor.getUsername(),
	                    distributor.getStoreUserContact(),
	                    distributor.getStoreUserEmail());
	            dto.setDistributorInfo(dInfo);
	        });

	        return dto;
	    }).toList();

	    // === Count Query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<DistributorRetailerOrderHdrEntity> countRoot = countQuery.from(DistributorRetailerOrderHdrEntity.class);
	    List<Predicate> countPredicates = new ArrayList<>(predicates); // Reuse the same predicates
	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(enrichedList, pageable, total);
	}


}
