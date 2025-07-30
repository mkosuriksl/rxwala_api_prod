package com.kosuri.stores.handler;

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

import com.kosuri.stores.dao.CustomerOrderDetailsEntity;
import com.kosuri.stores.dao.CustomerOrderDetailsRepo;
import com.kosuri.stores.dao.CustomerOrderHdrEntity;
import com.kosuri.stores.dao.CustomerOrderHdrRepo;
import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.ApiResponse;
import com.kosuri.stores.model.dto.CustomerGetOrderResponseDTO;
import com.kosuri.stores.model.dto.CustomerOrderDetailsDto;
import com.kosuri.stores.model.dto.CustomerOrderRequestDto;
import com.kosuri.stores.model.dto.UpdateCustomerOrderRequestDto;
import com.kosuri.stores.model.enums.OrderStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class CustomerOrderMethodHandler {

	@Autowired
	private CustomerOrderHdrRepo orderHdrRepo;

	@Autowired
	private CustomerOrderDetailsRepo orderDetailsRepo;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private ModelMapper modelMapper;

	public CustomerOrderHdrEntity ceateCustomerOrderMethod(CustomerOrderRequestDto dto) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}
		Optional<StoreEntity> store = storeRepository.findById(dto.getStoreId());
		// Create order header
		CustomerOrderHdrEntity orderHdr = new CustomerOrderHdrEntity();
		orderHdr.setStatus(OrderStatus.PENDING);
		orderHdr.setStoreId(store.get().getId());
		orderHdr.setOrderDate(new Date());
		orderHdr.setUpdatedBy(login.get().getCId());
		orderHdr.setUpdatedDate(new Date());
		orderHdrRepo.save(orderHdr);

		// Create and save order details
		List<CustomerOrderDetailsDto> orderDetailsList = dto.getOrderDetailsList();
		String baseOrderId = orderHdr.getOrderId(); // e.g., OR2025846584
		int counter = 1;
		List<CustomerOrderDetailsEntity> detailEntities = new ArrayList<>();
		for (CustomerOrderDetailsDto orderDetails : orderDetailsList) {
			CustomerOrderDetailsEntity orderDetailsEntity = new CustomerOrderDetailsEntity();

			StockEntity stockEntity = stockRepository.findByItemCode(orderDetails.getItemCode());
			if (stockEntity != null) {
				orderDetailsEntity.setItemCode(stockEntity.getItemCode());
			}
			orderDetailsEntity.setStoreId(store.get().getId());
			orderDetailsEntity.setItemName(orderDetails.getItemName());
			orderDetailsEntity.setMrp(orderDetails.getMrp());
			orderDetailsEntity.setDiscount(orderDetails.getDiscount());
			orderDetailsEntity.setGst(orderDetails.getGst());
			orderDetailsEntity.setTotal(orderDetails.getTotal());
			orderDetailsEntity.setOrderQty(orderDetails.getOrderQty());
			orderDetailsEntity.setPrescriptionRequired(orderDetails.getPrescriptionRequired());
			String orderLineId = baseOrderId + "_" + String.format("%03d", counter++);
			orderDetailsEntity.setOrderlineId(orderLineId);
			orderDetailsEntity.setUserIdStoreIdItemCode(stockEntity.getUserIdStoreIdItemCode());
			orderDetailsEntity.setCustomerOrderOrderHdrEntity(orderHdr);
			orderDetailsEntity.setUpdatedBy(login.get().getCId());
			orderDetailsEntity.setUpdatedDate(new Date());
			orderDetailsEntity.setManufacturerName(orderDetails.getManufacturerName());
			orderDetailsEntity.setUserId(login.get().getCId());
			orderDetailsEntity.setUserIdStoreId(login.get().getCId() + "_" + store.get().getId());
			detailEntities.add(orderDetailsEntity);
			orderDetailsRepo.save(orderDetailsEntity);
		}

		return orderHdr;
	}

	public List<CustomerOrderDetailsEntity> updateOrderDetails(UpdateCustomerOrderRequestDto dto) {
		String orderId = dto.getOrderId();
		List<CustomerOrderDetailsEntity> updatedEntities = new ArrayList<>();

		for (CustomerOrderDetailsDto details : dto.getOrderDetailsList()) {
			String orderlineId = details.getOrderlineId();

			if (orderlineId == null || !orderlineId.contains("_")) {
				throw new RuntimeException("Invalid orderlineId format: " + orderlineId);
			}

			String prefix = orderlineId.split("_")[0];
			if (!prefix.equals(orderId)) {
				throw new RuntimeException(
						"Mismatch: orderlineId '" + orderlineId + "' does not belong to orderId '" + orderId + "'");
			}

			CustomerOrderDetailsEntity entity = orderDetailsRepo.findByOrderlineId(orderlineId)
					.orElseThrow(() -> new RuntimeException("OrderLineId not found: " + orderlineId));

			// Update fields
			entity.setOrderQty(details.getOrderQty());
			entity.setTotal(details.getTotal());
			CustomerOrderDetailsEntity saved = orderDetailsRepo.save(entity);
			updatedEntities.add(saved);
			// ----- Update SaleEntity -----
			SaleEntity saleOrder = saleRepository.findByUserIdStoreIdItemCodeOne(entity.getUserIdStoreIdItemCode());
			if (saleOrder != null) {
				saleOrder.setOrderId(saved.getCustomerOrderOrderHdrEntity().getOrderId());
				saleOrder.setTotal(entity.getTotal());
				saleOrder.setQty(details.getOrderQty().doubleValue());
				saleRepository.save(saleOrder);
			}

			StockEntity stockOrder = stockRepository.findByUserIdStoreIdItemCode(entity.getUserIdStoreIdItemCode());

			if (stockOrder == null) {
				throw new RuntimeException("No existing stock record found for: " + entity.getUserIdStoreIdItemCode());
			}
			stockOrder.setTotal(Double.toString(entity.getTotal()));
			stockOrder.setBalQuantity(stockOrder.getBalQuantity() - entity.getOrderQty().doubleValue());
			stockRepository.save(stockOrder);

		}

		return updatedEntities;
	}

	@Transactional
	public ApiResponse<CustomerGetOrderResponseDTO> getOrderDetail(String orderid, String orderlineId, String itemName,
			String manufacturerName, String itemCode, String storeId, String userIdStoreIdItemCode, String fromDate,
			String toDate, String userId, Pageable pageable) {

		Page<CustomerOrderDetailsEntity> page = fetchFilteredOrderDetails(orderid, orderlineId, itemName,
				manufacturerName, itemCode, storeId, userIdStoreIdItemCode, fromDate, toDate, userId, pageable);

		if (page.isEmpty()) {
			return new ApiResponse<>("No records found", false, null);
		}

		CustomerOrderDetailsEntity first = page.getContent().get(0);
		String orderId = first.getCustomerOrderOrderHdrEntity().getOrderId();

		CustomerGetOrderResponseDTO responseDTO = new CustomerGetOrderResponseDTO();
		responseDTO.setOrderId(orderId);
		responseDTO.setStoreId(first.getStoreId());
		// ✅ Filter list where orderlineId starts with orderId
		List<CustomerOrderDetailsDto> filteredDtoList = page.getContent().stream().filter(entity -> {
			String orderline = entity.getOrderlineId();
			return orderline != null && orderline.startsWith(orderId + "_");
		}).map(entity -> modelMapper.map(entity, CustomerOrderDetailsDto.class)).collect(Collectors.toList());

		responseDTO.setOrderDetailsList(filteredDtoList);
		responseDTO.setCurrentPage(page.getNumber());
		responseDTO.setPageSize(page.getSize());
		responseDTO.setTotalElements(filteredDtoList.size());
		responseDTO.setTotalPages(1); // since we filtered, the totalPages should be adjusted accordingly

		return new ApiResponse<>("Customer Order Method details retrieved successfully.", true, responseDTO);
	}

	private Page<CustomerOrderDetailsEntity> fetchFilteredOrderDetails(String orderid, String orderlineId,
			String itemName, String manufacturerName, String itemCode, String storeId, String userIdStoreIdItemCode,
			String fromDate, String toDate, String userId, Pageable pageable) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}
		String cId = login.get().getCId(); // Get customer ID from token

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomerOrderDetailsEntity> cq = cb.createQuery(CustomerOrderDetailsEntity.class);
		Root<CustomerOrderDetailsEntity> root = cq.from(CustomerOrderDetailsEntity.class);
		Join<CustomerOrderDetailsEntity, CustomerOrderHdrEntity> hdrJoin = root.join("customerOrderOrderHdrEntity",
				JoinType.INNER);

		List<Predicate> predicates = new ArrayList<>();

		if (orderid != null && !orderid.trim().isEmpty()) {
			predicates.add(cb.equal(hdrJoin.get("orderId"), orderid));
		}
		if (orderlineId != null && !orderlineId.trim().isEmpty())
			predicates.add(cb.equal(root.get("orderlineId"), orderlineId));

		if (itemName != null && !itemName.trim().isEmpty())
			predicates.add(cb.equal(root.get("itemName"), itemName));

		if (manufacturerName != null && !manufacturerName.trim().isEmpty())
			predicates.add(cb.equal(root.get("manufacturerName"), manufacturerName));

		if (itemCode != null && !itemCode.trim().isEmpty())
			predicates.add(cb.equal(root.get("itemCode"), itemCode));

		if (storeId != null && !storeId.trim().isEmpty())
			predicates.add(cb.equal(root.get("storeId"), storeId));

		if (userIdStoreIdItemCode != null && !userIdStoreIdItemCode.trim().isEmpty())
			predicates.add(cb.equal(root.get("userIdStoreIdItemCode"), userIdStoreIdItemCode));

		if (fromDate != null && toDate != null) {
			predicates.add(cb.between(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate),
					java.sql.Date.valueOf(toDate)));
		} else if (fromDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(fromDate)));
		} else if (toDate != null) {
			predicates.add(cb.lessThanOrEqualTo(hdrJoin.get("orderDate"), java.sql.Date.valueOf(toDate)));
		}
		predicates.add(cb.equal(root.get("userId"), cId));

		cq.where(predicates.toArray(new Predicate[0]));
		cq.orderBy(cb.desc(root.get("orderlineId")));

		TypedQuery<CustomerOrderDetailsEntity> query = entityManager.createQuery(cq);

		int totalRows = query.getResultList().size();

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<CustomerOrderDetailsEntity> resultList = query.getResultList();

		return new PageImpl<>(resultList, pageable, totalRows);
	}

	 public String deleteOrderByOrderId(String orderId) {
	        Optional<CustomerOrderHdrEntity> orderOpt = orderHdrRepo.findById(orderId);
	        if (orderOpt.isPresent()) {
	        	orderHdrRepo.delete(orderOpt.get()); // This deletes both header and details
	            return "Order with orderId " + orderId + " deleted successfully from both tables.";
	        } else {
	            throw new EntityNotFoundException("Order with ID " + orderId + " not found.");
	        }
	    }
}
