package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DtItemListRepo extends JpaRepository<DtItemList, String> {

	Optional<DtItemList> findByItemCode(String itemCode);

}
