package com.kosuri.stores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kosuri.stores.handler.AdminHandler;
import com.kosuri.stores.model.request.AdminRegisterRequest;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginUserResponse;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminHandler adminHandler;

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponse> loginAdmin(@RequestBody LoginUserRequest request) throws Exception {
		return new ResponseEntity<>(adminHandler.loginAdmin(request), HttpStatus.CREATED);
	}

	@PostMapping("/register")
	public ResponseEntity<GenericResponse> adminRegister(@RequestBody AdminRegisterRequest request) {
		return new ResponseEntity<>(adminHandler.adminRegister(request), HttpStatus.CREATED);
	}

}
