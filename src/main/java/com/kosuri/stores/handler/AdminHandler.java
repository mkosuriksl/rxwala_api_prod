package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.AdminEntity;
import com.kosuri.stores.dao.AdminRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.model.enums.UserType;
import com.kosuri.stores.model.request.AddTabStoreUserRequest;
import com.kosuri.stores.model.request.AdminRegisterRequest;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginUserResponse;
import com.kosuri.stores.utils.CurrentUser;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Service
public class AdminHandler {

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private TabStoreRepository storeRepository;

	public LoginUserResponse loginAdmin(LoginUserRequest request) throws Exception {
		return userHandler.loginUser(request);
	}

	public GenericResponse adminRegister(AdminRegisterRequest request) {

		String email = CurrentUser.getEmail();
		TabStoreUserEntity user = storeRepository.findByStoreUserEmail(email).get();
		if (!user.getUserType().equals("AU")) {
			throw new RuntimeException("You don't have authorization to access this API.");
		}
		Optional<AdminEntity> checkAdmin = adminRepository.findByPhoneOrEmail(request.getEmailId(),
				request.getMobileNo());
		GenericResponse genericResponse = new GenericResponse();
		if (checkAdmin.isPresent()) {
			if (checkAdmin.get().getEmailId().equals(request.getEmailId())) {
				throw new RuntimeException("Email Address is duplicate = " + request.getEmailId());
			}
			if (checkAdmin.get().getMobileNo().equals(request.getMobileNo())) {
				throw new RuntimeException("Phone Number is duplicate = " + request.getMobileNo());
			}
		}
		// TabStoreUserEntity userStoreEntity = getEntityFromStoreUserRequest(request);
		AdminEntity admin = mapToAdminEntity(request);
		addUser(request);
		adminRepository.save(admin);
		genericResponse.setResponseMessage("Admin added successfully and Otp Send to the User Email and Mobile");
		return genericResponse;
	}

	private void addUser(AdminRegisterRequest request) {
		AddTabStoreUserRequest addTabStoreUserRequest = new AddTabStoreUserRequest();
		addTabStoreUserRequest.setPassword(request.getPassword());
		addTabStoreUserRequest.setUserFullName(request.getName());
		addTabStoreUserRequest.setUserEmail(request.getEmailId());
		addTabStoreUserRequest.setUserPhoneNumber(request.getMobileNo());
		addTabStoreUserRequest.setUserType(UserType.AU.toString());
		addTabStoreUserRequest.setStoreAdminEmail(request.getEmailId());
		addTabStoreUserRequest.setStoreAdminMobile(request.getMobileNo());
		addTabStoreUserRequest.setStatus("active");
		addTabStoreUserRequest.setAddedBy("admin");
		try {
			userHandler.addUser(addTabStoreUserRequest);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong!!");
			// e.printStackTrace();
		}
	}

	private AdminEntity mapToAdminEntity(AdminRegisterRequest request) {
		AdminEntity admin = new AdminEntity();
		admin.setName(request.getName());
		admin.setEmailId(request.getEmailId());
		admin.setMobileNo(request.getMobileNo());
		admin.setPwd(BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray()));
		admin.setUserRole("AU");
		admin.setUpdatedBy("ADMIN");
		admin.setUpdatedDate(LocalDateTime.now());
		return admin;
	}

}
