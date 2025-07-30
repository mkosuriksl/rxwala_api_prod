package com.kosuri.stores.model.response;

import com.kosuri.stores.dao.RoleEntity;

import java.util.List;

public class GetAllRolesResponse extends GenericResponse {
    public List<RoleEntity> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleEntity> roleList) {
        this.roleList = roleList;
    }

    private List<RoleEntity> roleList;
}
