package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRoleRepository extends JpaRepository<TaskRoleEntity, TaskRoleId> {
	List<TaskRoleEntity> findByRoleId(String roleId);
}
