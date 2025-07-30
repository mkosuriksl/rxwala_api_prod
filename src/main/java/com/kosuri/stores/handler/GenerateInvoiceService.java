package com.kosuri.stores.handler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.GenerateInvoiceEntity;
import com.kosuri.stores.dao.GenerateInvoiceRepository;
import com.kosuri.stores.dao.PurchaseHeaderEntity;
import com.kosuri.stores.dao.PurchaseHeaderRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.GenerateInvoiceRequestDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class GenerateInvoiceService {
	@Autowired
    private GenerateInvoiceRepository invoiceRepository;
	
	@Autowired
	private PurchaseHeaderRepository purchaseHeaderRepository;
	
	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;
	
	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public GenerateInvoiceEntity generateAndSaveInvoice(GenerateInvoiceRequestDTO dto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
	    String invNumber = (dto.getInvNumber() != null && !dto.getInvNumber().isEmpty())
	                        ? dto.getInvNumber()
	                        : "INV" + System.currentTimeMillis();

	    // Check and update/create in generate_invoice
	    GenerateInvoiceEntity invoice = invoiceRepository.findById(invNumber).orElse(new GenerateInvoiceEntity());
	    invoice.setInvNumber(invNumber);
	    invoice.setPonumber(dto.getPonumber());
	    invoice.setAmount(dto.getAmount());
	    invoice.setStatus(dto.getStatus());
	    String updatedBy;
		updatedBy = loginStore.get().getUserId();
		invoice.setUpdatedBy(updatedBy);
	    invoice.setUpdatedDate(LocalDateTime.now());
	    invoiceRepository.save(invoice);

	    // Check and update/create in pharma_purchase_header
	    PurchaseHeaderEntity purchaseHeader = purchaseHeaderRepository.findById(invNumber)
	        .orElse(new PurchaseHeaderEntity());

	    purchaseHeader.setBillNo(invNumber);
	    purchaseHeader.setTotal(dto.getAmount());
	    // Optional: update other fields if needed
	    if (purchaseHeader.getDate() == null) purchaseHeader.setDate(new Date());
	    purchaseHeaderRepository.save(purchaseHeader);

	    return invoice;
	}

	@Transactional
	public GenerateInvoiceEntity updateInvoiceFields(GenerateInvoiceRequestDTO dto) {
	    if (dto.getInvNumber() == null || dto.getInvNumber().isEmpty()) {
	        throw new IllegalArgumentException("Invoice number must not be null or empty.");
	    }

	    // Fetch invoice entity
	    GenerateInvoiceEntity invoice = invoiceRepository.findById(dto.getInvNumber())
	        .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with number: " + dto.getInvNumber()));

	    // Get logged-in user
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);

	    if (loginStore.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. Only store users can update invoices.");
	    }

	    // Update GenerateInvoiceEntity fields
	    invoice.setAmount(dto.getAmount());
	    invoice.setStatus(dto.getStatus());
	    invoice.setUpdatedBy(loginStore.get().getUserId());
	    invoice.setUpdatedDate(LocalDateTime.now());
	    invoiceRepository.save(invoice);

	    // Update amount in PurchaseHeaderEntity
	    PurchaseHeaderEntity purchaseHeader = purchaseHeaderRepository.findById(dto.getInvNumber())
	        .orElseThrow(() -> new ResourceNotFoundException("Purchase header not found with Bill No: " + dto.getInvNumber()));

	    purchaseHeader.setTotal(dto.getAmount());
	    purchaseHeaderRepository.save(purchaseHeader);

	    return invoice;
	}


	public Page<GenerateInvoiceEntity> getInvoiceNumber(String invNumber, String ponumber, Double amount,
			String status,Pageable pageable) throws AccessDeniedException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<GenerateInvoiceEntity> query = cb.createQuery(GenerateInvoiceEntity.class);
		Root<GenerateInvoiceEntity> root = query.from(GenerateInvoiceEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (invNumber != null && !invNumber.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("invNumber"), invNumber));
		}
		if (ponumber != null && !ponumber.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("ponumber"), ponumber));
		}
		if (amount != null) {
			predicates.add(cb.equal(root.get("amount"), amount));
		}
		if (status != null && !status.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("status"), status));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<GenerateInvoiceEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<GenerateInvoiceEntity> countRoot = countQuery.from(GenerateInvoiceEntity.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (invNumber != null && !invNumber.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("invNumber"), invNumber));
		}
		if (ponumber != null && !ponumber.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("ponumber"), ponumber));
		}
		if (amount != null) {
			countPredicates.add(cb.equal(countRoot.get("amount"), amount));
		}
		if (status != null && !status.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("status"), status));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
