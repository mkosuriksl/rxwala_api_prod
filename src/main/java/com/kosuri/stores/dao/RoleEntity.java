package com.kosuri.stores.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "role_pharma")
public class RoleEntity {

	@Id
	@Column(name = "Roleid")
	private String roleId;

	@Column(name = "Role_Name", nullable = true, length = 45)
	private String roleName;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
