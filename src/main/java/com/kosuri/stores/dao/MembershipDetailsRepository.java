package com.kosuri.stores.dao;

import com.kosuri.stores.model.response.RenewalStoreMemberships;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipDetailsRepository extends JpaRepository<MembershipDetailsEntity, String> {

	Optional<MembershipDetailsEntity> findByOrderId(MembershipHdrEntity orderId);

	@Query("SELECT NEW com.kosuri.stores.model.response.RenewalStoreMemberships(md.planId as planId,md.storeId as storeId,"
			+ "md.noOfDays as noOfDays,mh.orderId as orderId, mh.isVerified as paymentStatus) FROM MembershipDetailsEntity md "
			+ "INNER JOIN md.orderId mh WHERE ((:planId  IS NULL OR :planId ='' OR md.planId = :planId) and (:storeId  IS NULL OR :storeId ='' OR md.storeId = :storeId)\n"
			+ " and (:orderId  IS NULL OR :orderId ='' OR mh.orderId = :orderId) and (:noOfDays  IS NULL OR :noOfDays ='' OR md.noOfDays = :noOfDays) and (:status  IS NULL OR mh.isVerified = :status))")
	List<RenewalStoreMemberships> findAllFields(Pageable pageable, String planId, String storeId, String orderId,
			String noOfDays, Boolean status);

	@Query("SELECT COUNT(md) FROM MembershipDetailsEntity md WHERE md.orderId.orderId = :orderId")
	long countMembershipDetailsByOrderId(@Param("orderId") String orderId);

}
