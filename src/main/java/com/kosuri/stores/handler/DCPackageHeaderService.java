package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.DCPackageHeader;
import com.kosuri.stores.dao.DCPackageHeaderHistory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DCPackageHeaderService {

	@PersistenceContext
	private EntityManager entityManager;

	public List<DCPackageHeader> getDCPackageHeader(String packageId,String userIdStoreId,String packageName, String totalAmount, String updatedBy,
			Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("packageId","userIdStoreId", "packageName","totalAmount", "updatedBy");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DCPackageHeader> query = cb.createQuery(DCPackageHeader.class);
		Root<DCPackageHeader> root = query.from(DCPackageHeader.class);
		List<Predicate> predicates = new ArrayList<>();

		if (packageId != null) {
			predicates.add(cb.equal(root.get("packageId"), packageId));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		if (packageName != null) {
			predicates.add(cb.equal(root.get("packageName"), packageName));
		}
		if (totalAmount != null) {
			predicates.add(cb.equal(root.get("totalAmount"), totalAmount));
		}
		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	public List<DCPackageHeaderHistory> getDCPackageHeaderHistory(String packageId,String userIdStoreId,String packageName, String totalAmount, String updatedBy,
			Map<String, String> requestParams) {

		List<String> expectedParams = Arrays.asList("packageId","userIdStoreId", "packageName","totalAmount", "updatedBy");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DCPackageHeaderHistory> query = cb.createQuery(DCPackageHeaderHistory.class);
		Root<DCPackageHeaderHistory> root = query.from(DCPackageHeaderHistory.class);
		List<Predicate> predicates = new ArrayList<>();

		if (packageId != null) {
			predicates.add(cb.equal(root.get("packageId"), packageId));
		}
		if (userIdStoreId != null) {
			predicates.add(cb.equal(root.get("userIdStoreId"), userIdStoreId));
		}
		if (packageName != null) {
			predicates.add(cb.equal(root.get("packageName"), packageName));
		}
		if (totalAmount != null) {
			predicates.add(cb.equal(root.get("totalAmount"), totalAmount));
		}
		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

}
