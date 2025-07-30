package com.kosuri.stores.handler;

import com.kosuri.stores.dao.RoleEntity;
import com.kosuri.stores.dao.RoleRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.response.GetAllRolesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class RoleHandler {
    @Autowired
    private RoleRepository roleRepository;


    public void createRoleEntityFromRequest(String roleId, String roleName){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(roleId);
        roleEntity.setRoleName(roleName);

        try {
            roleRepository.save(roleEntity);
        }catch(Exception e){
            System.out.println(e.getCause());
        }
    }

    public GetAllRolesResponse getAllRoles(){
        GetAllRolesResponse response = new GetAllRolesResponse();
        List<RoleEntity> roleList = new ArrayList<RoleEntity>();
        roleRepository.findAll().forEach(role -> roleList.add(role));
        response.setRoleList(roleList);
        return response;
    }

    public String getRoleIdFromRoleName(String name) throws Exception {
        Optional<RoleEntity> role = roleRepository.findByRoleName(name);
        if (role.isPresent()) {
            return role.get().getRoleId();
        }

        throw new APIException("Role not found");
    }


}

