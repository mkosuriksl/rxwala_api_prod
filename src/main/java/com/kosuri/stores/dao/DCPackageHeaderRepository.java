package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DCPackageHeaderRepository extends JpaRepository<DCPackageHeader, String> {

	List<DCPackageHeader> findByStoreIdIn(List<String> userIdStoreIds);

	List<DCPackageHeader> findByPackageIdAndPackageNameAndStoreIdAndUserId(String packageId, String packageName,
			String storeId, String userId);
	
	List<DCPackageHeader> findByPackageIdAndPackageNameAndStoreIdAndUserIdAndUserIdStoreId(String packageId, String packageName,
			String storeId, String userId,String userIdStoreId);

	List<DCPackageHeader> findByPackageId(String packageId);

	List<DCPackageHeader> findByPackageIdAndPackageName(String packageId, String packageName);

	List<DCPackageHeader> findByPackageName(String packageName);

	List<DCPackageHeader> findByPackageIdAndPackageNameAndStoreId(String PackageId, String PackageName, String StoreId);

	List<DCPackageHeader> findByPackageIdAndStoreId(String PackageId, String StoreId);

	List<DCPackageHeader> findByPackageNameAndStoreId(String PackageName, String StoreId);

	List<DCPackageHeader> findByStoreId(String StoreId);

	public List<DCPackageHeader> findByPackageIdAndPackageNameAndUserId(String packageId, String packageName,
			String userId);

	public List<DCPackageHeader> findByPackageIdAndStoreIdAndUserId(String packageId, String storeId, String userId);

	public List<DCPackageHeader> findByPackageNameAndStoreIdAndUserId(String packageName, String storeId,
			String userId);

	public List<DCPackageHeader> findByUserId(String userId);
	
	@Query("SELECT d FROM DCPackageHeader d WHERE d.userId = :userId")
	Optional<List<DCPackageHeader>> findByUserIdOne(@Param("userId") String userId);


	public List<DCPackageHeader> findAll();

	List<DCPackageHeader> findByPackageIdAndUserId(String packageId, String userId);

	List<DCPackageHeader> findByPackageNameAndUserId(String packageName, String userId);

	List<DCPackageHeader> findByStoreIdAndUserId(String storeId, String userId);

	List<DCPackageHeader> findByUserIdStoreId(String string);

}
