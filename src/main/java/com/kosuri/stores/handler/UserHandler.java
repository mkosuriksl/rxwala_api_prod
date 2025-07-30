package com.kosuri.stores.handler;

import at.favre.lib.crypto.bcrypt.BCrypt;

import com.kosuri.stores.config.JwtService;
import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.UserType;
import com.kosuri.stores.model.request.*;
import com.kosuri.stores.model.response.CreateStoreResponse;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginStoreAndStoreUserResponse;
import com.kosuri.stores.model.response.LoginUserResponse;

import io.micrometer.common.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Primary
@Service
public class UserHandler implements UserDetailsService {
	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private StoreHandler storeHandler;

	@Autowired
	private RoleHandler roleHandler;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private UserOTPRepository otpRepository;

	@Autowired
	private DeleteUserRepo deleteUserRepo;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private UserOTPRepository userOtpRepository;
	
	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	@Autowired
	private UserServiceCatgoryTableRepository userServiceCatgoryTableRepository;
	
	public boolean addUser(AddUserRequest request) throws Exception {
		if (!repositoryHandler.validateUser(request)) {
			return false;
		}
		StoreEntity userStoreEntity = getEntityFromUserRequest(request);
		try {
			repositoryHandler.addUser(userStoreEntity, request);
		} catch (DataIntegrityViolationException e) {
			throw new Exception(e.getCause().getCause().getMessage());
		}
		return true;
	}

	public CreateStoreResponse addUser(AddTabStoreUserRequest request) throws Exception {
		if (!repositoryHandler.validateStoreUser(request)) {
			throw new APIException("Unable to add store user.");
		}
		CreateStoreResponse response = new CreateStoreResponse();
		TabStoreUserEntity userStoreEntity = getEntityFromStoreUserRequest(request);
		boolean isUserAdded;
		try {
			isUserAdded = repositoryHandler.addStoreUser(userStoreEntity, request);
			if (isUserAdded) {
				response.setResponseMessage("User added successfully and Otp Send to the User Email and Mobile");
			}

		} catch (DataIntegrityViolationException e) {
			throw new Exception(e.getCause().getCause().getMessage());
		}
		return response;
	}

	private TabStoreUserEntity getEntityFromStoreUserRequest(AddTabStoreUserRequest request) {
		TabStoreUserEntity storeEntity = new TabStoreUserEntity();

		storeEntity.setStatus(request.getStatus());
		storeEntity.setUsername(request.getUserFullName());
		storeEntity.setStoreUserEmail(request.getUserEmail());
		storeEntity.setStoreUserContact(request.getUserPhoneNumber());
		storeEntity.setType(request.getStore());
		storeEntity.setStoreAdminContact(request.getStoreAdminMobile());
		storeEntity.setStoreAdminEmail(request.getStoreAdminEmail());
		storeEntity.setPassword(getEncryptedPassword(request.getPassword()));
		storeEntity.setUserType((null != request.getUserType()) ? request.getUserType() : UserType.SA.toString());
		storeEntity.setRegistrationDate(LocalDateTime.now());
		storeEntity.setUserId(genereateUserId());

		storeEntity.setAddedBy("admin");

		return storeEntity;
	}

	public GenericResponse changePassword(PasswordRequest request, boolean isForgetPassword) throws Exception {
		TabStoreUserEntity tabStoreUserEntity = repositoryHandler.getTabStoreUser(request.getEmailAddress(),
				request.getUserContactNumber());
		GenericResponse response = new GenericResponse();
		if (tabStoreUserEntity != null && request.getPassword().equals(request.getConfirmPassword())) {
			boolean isPasswordNotSame = checkPassword(request.getPassword(), tabStoreUserEntity.getPassword());

			if (isPasswordNotSame) {
				if (isForgetPassword) {
					updatePassword(request, tabStoreUserEntity, response);
				} else {
					response.setResponseMessage("Password is same. Please set a new Password");
				}
			} else {
				updatePassword(request, tabStoreUserEntity, response);
			}
			return response;
		}
		return response;
	}

	private void updatePassword(PasswordRequest request, TabStoreUserEntity tabStoreUserEntity,
			GenericResponse response) {
		tabStoreUserEntity.setPassword(getEncryptedPassword(request.getPassword()));
		boolean isPasswordUpdated = repositoryHandler.updatePassword(tabStoreUserEntity);
		if (isPasswordUpdated) {
			response.setResponseMessage("Password Updated Successfully");
		} else {
			response.setResponseMessage("Unable To Update Password.");
		}
	}

	public GenericResponse forgetPassword(PasswordRequest request) throws Exception {
		GenericResponse response = new GenericResponse();
		if (!StringUtils.isEmpty(request.getEmailAddress()) || !StringUtils.isEmpty(request.getUserContactNumber())) {
			TabStoreUserEntity tabStoreUserEntity = repositoryHandler.getTabStoreUser(request.getEmailAddress(),
					request.getUserContactNumber());
			OTPRequest otpRequest = new OTPRequest();
			if (null != tabStoreUserEntity) {
				otpRequest.setIsForgetPassword(true);
				sendOTP(request, otpRequest);
				response.setResponseMessage("Forget Password Initiated");
			} else {
				response.setResponseMessage("Unable to Initiated Forget Password.");
			}
		}
		return response;
	}

	public GenericResponse verifyOTPAndChangePassword(PasswordRequest request) throws Exception {
		GenericResponse genericResponse = new GenericResponse();
		if (!StringUtils.isEmpty(request.getEmailAddress()) || !StringUtils.isEmpty(request.getUserContactNumber())) {
			boolean isOtpVerified = false;
			VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest();
			verifyOTPRequest.setOtp(request.getOtp());
			verifyOTPRequest.setIsForgetPassword(true);
			if (null != request.getEmailAddress()) {
				verifyOTPRequest.setEmail(request.getEmailAddress());
				isOtpVerified = verifyEmailOTP(verifyOTPRequest);
			} else if (!StringUtils.isEmpty(request.getUserContactNumber())) {
				verifyOTPRequest.setPhoneNumber(request.getUserContactNumber());
				isOtpVerified = verifySmsOTP(verifyOTPRequest);
			}
			if (isOtpVerified) {
				genericResponse = changePassword(request, true);
			}
			genericResponse
					.setResponseMessage("Your password has been changed successfully. Use your new password to login");
		}
		return genericResponse;
	}

	private boolean sendOTP(PasswordRequest request, OTPRequest otpRequest) {

		if (null != request.getEmailAddress()) {
			otpRequest.setEmail(request.getEmailAddress());
			return sendEmailOtp(otpRequest);
		} else {
			otpRequest.setPhoneNumber(request.getUserContactNumber());
			return sendOTPToPhone(otpRequest);
		}
	}

	private boolean checkPassword(String plainPassword, String hashedPassword) {
		return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
	}

	public String getEncryptedPassword(String password) {
		return BCrypt.withDefaults().hashToString(12, password.toCharArray());
	}

	private String genereateUserId() {
		LocalDateTime timestamp = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestampStr = timestamp.format(formatter);
		return StoreConstants.RX_CONSTANT + "_" + timestampStr + "_" + OtpHandler.generateOTP(false);
	}

	public LoginUserResponse loginUser(LoginUserRequest request) throws Exception {
		LoginUserResponse response = new LoginUserResponse();
		if ((request.getEmail() == null || request.getEmail().isEmpty())
				&& (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
			throw new APIException("Either email or phone number must be provided, both can't be null/empty");
		}
		TabStoreUserEntity tabStoreUserEntity = repositoryHandler.loginUser(request);
		UserDetails storeUser = loadUserByUsername(tabStoreUserEntity.getStoreUserEmail());

		Optional<UserOTPEntity> otp = otpRepository.findbyEmailAndPhone(tabStoreUserEntity.getStoreUserEmail(),
				tabStoreUserEntity.getStoreUserContact());

		if (otp.isPresent()) {
			if (request.getEmail() != null && request.getEmail().equals(otp.get().getUserEmail())
					&& !otp.get().isEmailVerify()) {
				throw new APIException("Email not verified. Please verify at least one to proceed.");
			}

			if (request.getPhoneNumber() != null && request.getPhoneNumber().equals(otp.get().getUserPhoneNumber())
					&& !otp.get().isSmsVerify()) {
				throw new APIException("Mobile number  not verified. Please verify at least one to proceed.");
			}
		} else {
			throw new APIException("OTP not found.");
		}
		if (null != tabStoreUserEntity) {
			response.setUserId(tabStoreUserEntity.getUserId());
			response.setUsername(tabStoreUserEntity.getUsername());
			response.setUserType(tabStoreUserEntity.getUserType());
			response.setUserEmailAddress(tabStoreUserEntity.getStoreUserEmail());
			response.setUserContact(tabStoreUserEntity.getStoreUserContact());
			String jwtToken = jwtService.generateToken(storeUser);
			Token token = new Token();
			token.setToken(jwtToken);
			token.setUserId(tabStoreUserEntity.getUserId());
			tokenRepository.save(token);
			response.setResponseMessage(tabStoreUserEntity.getUserId());
			response.setToken(jwtToken);
			Optional<UserServiceCatgoryTable> userServiceCatgoryTable =
			        userServiceCatgoryTableRepository.findByUserId(tabStoreUserEntity.getUserId());

			    userServiceCatgoryTable.ifPresent(serviceCategory -> 
			        response.setDashboardRole(serviceCategory.getDashboardRole())
			    );
		}
		return response;
	}
	//storeAndStoreUser
	public LoginStoreAndStoreUserResponse loginStoreAndStoreUser(LoginUserRequest request) throws Exception {
		LoginStoreAndStoreUserResponse response = new LoginStoreAndStoreUserResponse();

	    if ((request.getEmail() == null || request.getEmail().isEmpty()) &&
	        (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
	        throw new APIException("Either email or phone number must be provided, both can't be null/empty");
	    }

	    // Fetch user details from the repository
	    TabStoreUserEntity tabStoreUserEntity = repositoryHandler.loginUser(request);

	    if (tabStoreUserEntity == null) {
	        throw new APIException("Invalid credentials. User not found.");
	    }

	    // Check if user is active before allowing login
	    if (!"Active".equalsIgnoreCase(tabStoreUserEntity.getStatus())) {
	        throw new APIException("User is not active. Please contact support.");
	    }

	    // **Restrict access to only userType = "SU"**
	    if (!"SU".equalsIgnoreCase(tabStoreUserEntity.getUserType())) {
	        throw new APIException("Access denied. Only SU users are allowed to log in.");
	    }

	    // Load user details for JWT token generation
	    UserDetails storeUser = loadUserByUsername(tabStoreUserEntity.getStoreUserEmail());

	    // Set user details in response
	    response.setUserId(tabStoreUserEntity.getUserId());
	    response.setUsername(tabStoreUserEntity.getUsername());
	    response.setUserType(tabStoreUserEntity.getUserType());
	    response.setUserEmailAddress(tabStoreUserEntity.getStoreUserEmail());
	    response.setUserContact(tabStoreUserEntity.getStoreUserContact());

	    // Generate JWT token
	    String jwtToken = jwtService.generateToken(storeUser);
	    Token token = new Token();
	    token.setToken(jwtToken);
	    token.setUserId(tabStoreUserEntity.getUserId());
	    tokenRepository.save(token);

	    response.setResponseMessage("Login Successful");
	    response.setToken(jwtToken);

	    return response;
	}



	private StoreEntity getEntityFromUserRequest(AddUserRequest request) {
		StoreEntity storeEntity = new StoreEntity();
		storeEntity.setOwner(request.getName());
		storeEntity.setOwnerContact(request.getPhoneNumber());
		storeEntity.setOwnerEmail(request.getEmail());
		storeEntity.setLocation(request.getAddress());
		storeEntity.setRole(request.getRole());
		storeEntity.setCreationTimeStamp(LocalDateTime.now().toString());
		storeEntity.setAddedBy("admin");

		return storeEntity;
	}

	public boolean verifyEmailOTP(VerifyOTPRequest emailOtp) throws APIException {
		return repositoryHandler.verifyEmailOtp(emailOtp);
	}

	public boolean verifySmsOTP(VerifyOTPRequest smsOtp) {
		return repositoryHandler.verifyPhoneOtp(smsOtp);
	}

	public boolean sendEmailOtp(OTPRequest request) {
		return repositoryHandler.sendEmailOtp(request);
	}

	public boolean sendOTPToPhone(OTPRequest request) {
		return repositoryHandler.sendOtpToSMS(request);
	}

	/**
	 * @param username the username identifying the user whose data is required.
	 * @return
	 * @throws UsernameNotFoundException
	 */
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		TabStoreUserEntity customerRegistration;
//		if (isValidEmail(username)) {
//			customerRegistration = tabStoreRepository.findByStoreUserEmail(username)
//					.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//		} else {
//			customerRegistration = tabStoreRepository.findByStoreUserContact(username)
//					.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//		}
//
//		Set<GrantedAuthority> authorities = new HashSet<>();
//		authorities.add(new SimpleGrantedAuthority("ROLE_" + customerRegistration.getUserType()));
//
//		return User.withUsername(customerRegistration.getStoreUserEmail()).password(customerRegistration.getPassword())
//				.authorities(authorities).build();
//	}

	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    TabStoreUserEntity tabStoreUser = null;
	    CustomerRegisterEntity customerUser = null;
	    
	    // Check in TabStoreUserEntity
	    if (isValidEmail(username)) {
	        tabStoreUser = tabStoreRepository.findByStoreUserEmail(username).orElse(null);
	    } else {
	        tabStoreUser = tabStoreRepository.findByStoreUserContact(username).orElse(null);
	    }
	    
	    // If not found in TabStore, check in CustomerRegisterEntity
	    if (tabStoreUser == null) {
	        customerUser = customerRegisterRepository.findByEmailOrPhoneNumber(username).orElse(null);
	    }

	    // If neither entity is found, throw exception
	    if (tabStoreUser == null && customerUser == null) {
	        throw new UsernameNotFoundException("User Not Found");
	    }

	    // Determine the correct entity and return UserDetails
	    Set<GrantedAuthority> authorities = new HashSet<>();
	    String email;
	    String password;
	    
	    if (tabStoreUser != null) {
	        authorities.add(new SimpleGrantedAuthority("ROLE_" + tabStoreUser.getUserType()));
	        email = tabStoreUser.getStoreUserEmail();
	        password = tabStoreUser.getPassword();
	    } else {
	        authorities.add(new SimpleGrantedAuthority("ROLE_" + customerUser.getUserType()));
	        email = customerUser.getEmail();
	        password = customerUser.getPassword();
	    }

	    return User.withUsername(email).password(password).authorities(authorities).build();
	}

	private boolean isValidEmail(String input) {
		return input != null && input.contains("@");
	}

	@Transactional
	public GenericResponse servicePersonDeletAccout(DeleteAccountRequest accountRequest) {
		GenericResponse response = new GenericResponse();
		try {
			TabStoreUserEntity entity = tabStoreRepository.findById(accountRequest.getUserId())
					.orElseThrow(() -> new APIException(" User Not Found By : " + accountRequest.getUserId()));
			if (entity == null) {
				response.setResponseMessage("Account not found.!");
				return response;
			}
			List<StoreEntity> storeInfos = storeRepository.findByOwnerEmail(entity.getStoreUserEmail()).get();
			UserOTPEntity otpEntity = userOtpRepository.findByUserEmail(entity.getStoreUserEmail()).get();
			DeleteUser deleteUser = new DeleteUser();
			deleteUser.setDeletedDate(LocalDateTime.now());
			deleteUser.setDeleteReason(accountRequest.getReason());
			deleteUser.setEmail(entity.getStoreUserEmail());
			deleteUser.setPhone(entity.getStoreUserContact());
			deleteUser.setDeactivated(true);
			deleteUser.setCandidateId(entity.getUserId());
			deleteUserRepo.save(deleteUser);
			response.setResponseMessage("Account deleted. Thank you for being with us.");
			response.setDetails(storeInfos);
			userOtpRepository.delete(otpEntity);
			tabStoreRepository.delete(entity);
			storeRepository.deleteAll(storeInfos);
			return response;
		} catch (APIException e) {
			e.printStackTrace();
		}
		return null;
	}

}
