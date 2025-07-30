package com.kosuri.stores.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterRequest {

	@NotBlank(message = "Full Name is required.")
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Full Name must contain only letters and spaces.")
	@Size(max = 50, message = "Full Name must not exceed 50 characters.")
	private String name;

	@NotBlank(message = "Email is required.")
	@Email(message = "Email should be valid.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Email should be valid and contain only allowed characters.")
	@Size(max = 50, message = "Email must not exceed 50 characters.")
	private String emailId;

	@NotBlank(message = "Phone Number is required.")
	@Pattern(regexp = "^\\d{15}$", message = "Phone Number must be exactly 15 digits.")
	private String mobileNo;

	@NotBlank(message = "Password is required.")
	private String password;

	private String workLocation;

}
