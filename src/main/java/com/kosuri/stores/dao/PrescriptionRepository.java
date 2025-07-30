package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, String> {

	Optional<Prescription> findByVisitOrdNo(String visitOrdNo);
}
