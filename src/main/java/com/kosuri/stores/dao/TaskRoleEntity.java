package com.kosuri.stores.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "taks_role_mapping")
@IdClass(TaskRoleId.class)
public class TaskRoleEntity {

	@Id
	@Column(name = "Task_ID")
	private Integer taskId;

	@Column(name = "Task_Name", nullable = true, length = 45)
	private String taskName;

	@Column(name = "RoleId", nullable = true, length = 45)
	@Id
	private String roleId;

	@Column(name = "Updated_Date", nullable = true, length = 45)
	private String updatedDate;

	@Column(name = "Updated_By", nullable = true, length = 45)
	private String updatedBy;

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
