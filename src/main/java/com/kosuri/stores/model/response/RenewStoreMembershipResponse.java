package com.kosuri.stores.model.response;

import java.util.List;

import lombok.Data;

@Data
public class RenewStoreMembershipResponse extends GenericResponse{

    private List<RenewalStoreMemberships> renewalStoreMembershipList;

}
