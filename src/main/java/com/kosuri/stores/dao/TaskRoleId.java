package com.kosuri.stores.dao;

import java.io.Serializable;
import java.util.Objects;

public class TaskRoleId implements Serializable {
	private Integer taskId;
	private String roleId;

	public TaskRoleId() {
	}

	public TaskRoleId(Integer taskId, String roleId) {
		this.taskId = taskId;
		this.roleId = roleId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TaskRoleId that = (TaskRoleId) o;
		return Objects.equals(taskId, that.taskId) && Objects.equals(roleId, that.roleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(taskId, roleId);
	}
}
