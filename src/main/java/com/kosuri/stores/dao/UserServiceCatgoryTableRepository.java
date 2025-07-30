package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserServiceCatgoryTableRepository extends JpaRepository<UserServiceCatgoryTable, Long> {

	Optional<UserServiceCatgoryTable> findByUserId(String userId);

}
