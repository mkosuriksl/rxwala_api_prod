package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kosuri.stores.config.JwtService;
import com.kosuri.stores.dao.CustomerLogInRepository;
import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.CustomerRegistrationInfoDto;
import com.kosuri.stores.dao.CustomerWalkInUpdateRequest;
import com.kosuri.stores.dao.DCBookingRequestHeader;
import com.kosuri.stores.dao.DCBookingRequestHeaderHistory;
import com.kosuri.stores.dao.DCBookingRequestHeaderHistoryRepository;
import com.kosuri.stores.dao.DCBookingRequestHeaderRepository;
import com.kosuri.stores.dao.DCPackageHeader;
import com.kosuri.stores.dao.DCPackageHeaderHistory;
import com.kosuri.stores.dao.DCPackageHeaderHistoryRepository;
import com.kosuri.stores.dao.DCPackageHeaderRepository;
import com.kosuri.stores.dao.DeleteUser;
import com.kosuri.stores.dao.DeleteUserRepo;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.dao.Token;
import com.kosuri.stores.dao.TokenRepository;
import com.kosuri.stores.dao.WalkInCustomerEntity;
import com.kosuri.stores.dao.WalkInCustomerRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.CustomerRegisterDto;
import com.kosuri.stores.model.dto.CustomerUpdateRequest;
import com.kosuri.stores.model.dto.WalkInCustomerRequestDto;
import com.kosuri.stores.model.request.DeleteAccountRequest;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.LoginUserResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class CustomerHandler implements UserDetailsService{
	
	@Autowired
	private WalkInCustomerRepository walkInCustomerRepository;
	@Autowired
	private TabStoreRepository tabStoreRepository;
	@PersistenceContext
	private EntityManager entityManager;
    @Autowired
    private CustomerRegisterRepository registerRepository;
    @Autowired
    private CustomerLogInRepository logInRepository;
    @Autowired
    private UserHandler userHandler;
	@Autowired
	private RepositoryHandler repositoryHandler;
	
	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private OtpHandler otpHandler;
	
	@Autowired
	private DCBookingRequestHeaderRepository dcbookingRequestHeaderRepository;
	
	@Autowired
	private DCBookingRequestHeaderHistoryRepository dcBookingRequestHeaderHistoryRepository;
	
	@Autowired
	private DeleteUserRepo deleteUserRepo;
	
	@Autowired 
	private DCPackageHeaderRepository dcPackageHeaderRepository;
	
	@Autowired
	private DCPackageHeaderHistoryRepository dcPackageHeaderHistoryRepository;
//    public CustomerRegisterDto customerRegister(CustomerRegisterDto customerDto) {
//        CustomerRegisterEntity register = new CustomerRegisterEntity();
//        register.setName(customerDto.getName());
//        register.setEmail(customerDto.getEmail());
//        register.setPhoneNumber(customerDto.getPhoneNumber());
//        register.setPassword(userHandler.getEncryptedPassword(customerDto.getPassword()));
//        register.setLocation(customerDto.getLocation());
////        register.setCId(customerDto.getCId());
//        Date currentDate = new Date();
//        register.setRegisteredDate(currentDate);
//        register.setUpdatedDate(currentDate);
//        register.setStatus(customerDto.isStatus());
//
//        if (registerRepository.existsByEmail(register.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        CustomerRegisterEntity saveCustomer = registerRepository.save(register);
//
//
//        CustomerLogInEntity customerLogin = new CustomerLogInEntity();
//        customerLogin.setName(saveCustomer.getName());
//        customerLogin.setPhoneNumber(saveCustomer.getPhoneNumber());
//        customerLogin.setCustomerId(saveCustomer.getCId());
//        customerLogin.setEmail(saveCustomer.getEmail());
////        customerLogin.setPassword(userHandler.getEncryptedPassword(saveCustomer.getPassword()));
//        customerLogin.setPassword(saveCustomer.getPassword());
//        customerLogin.setEmailVerified(saveCustomer.isStatus());
//        customerLogin.setMobileVerified(saveCustomer.isStatus());
//        customerLogin.setUpdatedDate(saveCustomer.getUpdatedDate());
//        // Set other fields as needed
//
//        logInRepository.save(customerLogin);
//        customerDto.setId(saveCustomer.getId()); 
//        customerDto.setCId(saveCustomer.getCId());
//        customerDto.setRegisteredDate(saveCustomer.getRegisteredDate());
//        customerDto.setUpdatedDate(saveCustomer.getUpdatedDate());
//        customerDto.setStatus(saveCustomer.isStatus());
//
//        return customerDto;
//    }

    public CustomerRegisterDto getByEmail(String email){
        CustomerRegisterEntity customerRegister=registerRepository.findByEmail(email).
                orElseThrow(()-> new RuntimeException("Email Not Found"));
        return convertToDto(customerRegister);
    }

    private CustomerRegisterDto convertToDto (CustomerRegisterEntity register){
        CustomerRegisterDto dto = new CustomerRegisterDto();
        dto.setCId(register.getCId());
        dto.setName(register.getName());
        dto.setEmail(register.getEmail());
        dto.setPhoneNumber(register.getPhoneNumber());
        dto.setRegDate(register.getRegisteredDate());
        dto.setUpdatedDate(register.getUpdatedDate());
        dto.setUserType(register.getUserType());
        dto.setLocation(register.getLocation());        
        return dto;
    }

	public CustomerRegisterDto addCustomerRegistration(CustomerRegisterEntity dbl) {
		CustomerRegisterEntity existingProfile = registerRepository.findByCidOrEmailOrPhoneNumber(
				dbl.getCId(), dbl.getEmail(), dbl.getPhoneNumber());
		if (existingProfile == null) {
			BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
			String encryptPassword = byCrypt.encode(dbl.getPassword());
			dbl.setPassword(encryptPassword);
			dbl.setEmailOtp(null);
			dbl.setUserType("cu");
			dbl.setEmailVerify("no");
			dbl.setMobileOtp(null);
			dbl.setMobileVerify("no");
			dbl.setCustomerStatus("active");

			CustomerRegisterEntity saved = registerRepository.save(dbl);

			CustomerRegisterDto dblDto = new CustomerRegisterDto();

			dblDto.setEmail(dbl.getEmail());
			dblDto.setPhoneNumber(dbl.getPhoneNumber());
			dblDto.setName(dbl.getName());
			dblDto.setRegDate(dbl.getRegisteredDate());
			dblDto.setUserType(dbl.getUserType());
			dblDto.setLocation(dbl.getLocation());
			dblDto.setCId(dbl.getCId());
			dblDto.setAddress(dbl.getAddress());
			dblDto.setRegisterMode(dbl.getRegisterMode());
			return dblDto;
		}
		return null;
	}
	
	public CustomerRegisterDto addWalkInCustomerRegistration(WalkInCustomerRequestDto dto) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
		}
		
		CustomerRegisterEntity existingProfile = registerRepository.findByCidOrEmailOrPhoneNumber(
				null, dto.getEmail(), dto.getPhoneNumber());
		if (existingProfile == null) {
			CustomerRegisterEntity customer = new CustomerRegisterEntity();
			customer.setName(dto.getName());
			customer.setEmail(dto.getEmail());
			customer.setPhoneNumber(dto.getPhoneNumber());
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			customer.setPassword(encoder.encode(dto.getPassword()));
			customer.setAddress(dto.getAddress());
			customer.setLocation(dto.getLocation());
			customer.setUserType("cu");
			customer.setCustomerStatus("active");
			customer.setEmailOtp(null);
			customer.setMobileOtp(null);
			customer.setEmailVerify("no");
			customer.setMobileVerify("no");
			customer.setRegisterMode("WI");
			customer.setUpdatedBy(loginStore.get().getUserId());
			CustomerRegisterEntity saved = registerRepository.save(customer);
			
			WalkInCustomerEntity walkInCustomer = new WalkInCustomerEntity();
	        walkInCustomer.setCId(saved.getCId()); // use the same cId
	        walkInCustomer.setStoreId(dto.getStoreId()); // or use loginStore.get().getStoreId() if needed
	        walkInCustomer.setUpdatedBy(saved.getUpdatedBy());
	        WalkInCustomerEntity walkSaved=walkInCustomerRepository.save(walkInCustomer);

			CustomerRegisterDto dblDto = new CustomerRegisterDto();

			dblDto.setEmail(saved.getEmail());
			dblDto.setPhoneNumber(saved.getPhoneNumber());
			dblDto.setName(saved.getName());
			dblDto.setRegDate(saved.getRegisteredDate());
			dblDto.setUserType(saved.getUserType());
			dblDto.setLocation(saved.getLocation());
			dblDto.setCId(saved.getCId());
			dblDto.setAddress(saved.getAddress());
			dblDto.setRegisterMode(saved .getRegisterMode());
			dblDto.setUpdatedBy(saved.getUpdatedBy());
			dblDto.setStoreId(walkSaved.getStoreId());
			return dblDto;
		}
		return null;
	}

	public String isEmailExists(String email) {

		if (registerRepository.findByEmail(email) != null) {
			return email;
		} else {

		}
		return null;
	}
	
	public CustomerRegisterEntity updateData(String otp, String email) {
		Optional<CustomerRegisterEntity> existedById = registerRepository.findByEmail(email);

		if (existedById.isPresent()) {

			existedById.get().setEmailOtp(otp);
			existedById.get().setEmailVerify("yes");

			return registerRepository.save(existedById.get());
		}
		return null;
	}
	
	public CustomerRegisterEntity updateDataWithMobile(String Otp, String dlerMobileNo) {
		Optional<CustomerRegisterEntity> existedById = registerRepository.findByPhoneNumber(dlerMobileNo);
		if(existedById.isPresent()) {
			existedById.get().setMobileOtp(Otp);
			updateDataWithMobileMethod(dlerMobileNo);
			return registerRepository.save(existedById.get());
		}
		return null;
	}
	
	public CustomerRegisterEntity updateDataWithMobileMethod(String dlerMobileNo) {
		Optional<CustomerRegisterEntity> existedById =registerRepository.findByPhoneNumber(dlerMobileNo);

		if (existedById.isPresent()) {
			existedById.get().setMobileVerify("yes");
			existedById.get().setCustomerStatus("active");
			return registerRepository.saveAndFlush(existedById.get());
		}
		return null;
	}

	public LoginUserResponse loginCustomerUser(LoginUserRequest request) throws Exception {
		LoginUserResponse response = new LoginUserResponse();
		if ((request.getEmail() == null || request.getEmail().isEmpty())
				&& (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
			throw new APIException("Either email or phone number must be provided, both can't be null/empty");
		}
		CustomerRegisterEntity tabStoreUserEntity = repositoryHandler.loginCustomerUser(request);
		if (!"yes".equalsIgnoreCase(tabStoreUserEntity.getEmailVerify()) 
	            && !"yes".equalsIgnoreCase(tabStoreUserEntity.getMobileVerify())) {
	        throw new APIException("User email or mobile verification is required before login.");
	    }
		UserDetails storeUser = loadUserByUsername(tabStoreUserEntity.getEmail());

		if (null != tabStoreUserEntity) {
			response.setUserId(tabStoreUserEntity.getCId());
			response.setUsername(tabStoreUserEntity.getName());
			response.setUserType(tabStoreUserEntity.getUserType());
			response.setUserEmailAddress(tabStoreUserEntity.getEmail());
			response.setUserContact(tabStoreUserEntity.getPhoneNumber());
			String jwtToken = jwtService.generateUserToken(storeUser);
			Token token = new Token();
			token.setToken(jwtToken);
			token.setUserId(tabStoreUserEntity.getCId());
			tokenRepository.save(token);
			response.setResponseMessage(tabStoreUserEntity.getCId());
			response.setToken(jwtToken);
		}
		return response;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CustomerRegisterEntity customerRegistration = registerRepository.findByEmailOrPhoneNumber(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + customerRegistration.getUserType()));

		return User.withUsername(customerRegistration.getEmail()).password(customerRegistration.getPassword())
				.authorities(authorities).build();
	}
	
	private boolean isValidEmail(String input) {
		return input != null && input.contains("@");
	}
	
	public String changePassword(String email, String oldPassword, String newPassword, String confirmPassword,
			String mobileNo) {

		Optional<CustomerRegisterEntity> user = registerRepository.findByEmail(email);
		if (user.isPresent()) {
			BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
			if (byCrypt.matches(oldPassword, user.get().getPassword())) {
				if (newPassword.equals(confirmPassword)) {
					String encryptPassword = byCrypt.encode(confirmPassword);
					user.get().setPassword(encryptPassword);
					registerRepository.save(user.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else {
			return "invalid";
		}

	}
	
	public String sendMail(String email) {
		Optional<CustomerRegisterEntity> userOp = registerRepository.findByEmail(email);
		if (userOp.isPresent()) {
			otpHandler.generateOtp(email, "forgotPassword");
			return "otp";
		}
		return null;
	}
	
	public String sendSms(String mobileNo) {
		Optional<CustomerRegisterEntity> userOp =
				registerRepository.findByPhoneNumber(mobileNo);
		if (userOp.isPresent()) {
			otpHandler.generateMobileOtp(mobileNo);
			return "otp";
		}
		return null;
	}
	
	public String forgetPassword(String email, String otp, String newPassword, String confirmPassword,
			String mobileNo) {

		Optional<CustomerRegisterEntity> userEmail = registerRepository.findByEmail(email);
		Optional<CustomerRegisterEntity> userMobile = 
				registerRepository.findByPhoneNumber(mobileNo);
		if (userEmail.isPresent()) {
			if (otpHandler.verifyOtp(email, otp)) {
				if (newPassword.equals(confirmPassword)) {
					BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
					String encryptPassword = byCrypt.encode(confirmPassword);
					userEmail.get().setPassword(encryptPassword);
					registerRepository.save(userEmail.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else if (userMobile.isPresent()) {
			if (otpHandler.verifyMobileOtp(mobileNo, otp)) {
				if (newPassword.equals(confirmPassword)) {
					BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
					String encryptPassword = byCrypt.encode(confirmPassword);
					userMobile.get().setPassword(encryptPassword);
					registerRepository.save(userMobile.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else if (!userEmail.isPresent() && userMobile.isPresent()) {
			return "incorrectEmail";
		}
		return null;

	}
	
	@Transactional
	public GenericResponse servicePersonDeletAccout(DeleteAccountRequest accountRequest) {
		GenericResponse response = new GenericResponse();
		try {
			CustomerRegisterEntity entity = registerRepository.findByCid(accountRequest.getUserId())
					.orElseThrow(() -> new APIException(" User Not Found By : " + accountRequest.getUserId()));
			if (entity == null) {
				response.setResponseMessage("Account not found.!");
				return response;
			}
			List<DCBookingRequestHeader> dcHeader = dcbookingRequestHeaderRepository.findByCustomerId(entity.getCId()).get();
			List<DCBookingRequestHeaderHistory> dcHeaderHistory= dcBookingRequestHeaderHistoryRepository.findByCustomerId(entity.getCId()).get();
			
			List<DCPackageHeader> dcPackageHeader=dcPackageHeaderRepository.findByUserIdOne(entity.getCId()).get();
			List<DCPackageHeaderHistory> dcPackageHeaderHistory=dcPackageHeaderHistoryRepository.findByUserId(entity.getCId()).get();
			DeleteUser deleteUser = new DeleteUser();
			deleteUser.setDeletedDate(LocalDateTime.now());
			deleteUser.setDeleteReason(accountRequest.getReason());
			deleteUser.setEmail(entity.getEmail());
			deleteUser.setPhone(entity.getPhoneNumber());
			deleteUser.setDeactivated(true);
			deleteUser.setCandidateId(entity.getCId());
			deleteUserRepo.save(deleteUser);
			response.setResponseMessage("Account deleted. Thank you for being with us.");
			response.setDetails(entity);
			dcbookingRequestHeaderRepository.deleteAll(dcHeader);
			registerRepository.delete(entity);
			dcBookingRequestHeaderHistoryRepository.deleteAll(dcHeaderHistory);
			dcPackageHeaderRepository.deleteAll(dcPackageHeader);
			dcPackageHeaderHistoryRepository.deleteAll(dcPackageHeaderHistory);
			return response;
		} catch (APIException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CustomerRegisterDto updateCustomerDetails(CustomerUpdateRequest request) {
        Optional<CustomerRegisterEntity> optionalCustomer = registerRepository.findByCid(request.getCustomerId());

        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer with cId " + request.getCustomerId() + " not found");
        }

        CustomerRegisterEntity customer = optionalCustomer.get();
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setUpdatedDate(new Date());
        registerRepository.save(customer);

        CustomerRegisterDto response = new CustomerRegisterDto();
        response.setCId(customer.getCId());
        response.setName(customer.getName());
        response.setAddress(customer.getAddress());
        response.setLocation(customer.getLocation());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setEmail(customer.getEmail());
        response.setRegDate(customer.getRegisteredDate());
        response.setUserType(customer.getUserType());
        response.setUpdatedDate(customer.getUpdatedDate());
        return response;
        
    }
	
	public Page<CustomerRegistrationInfoDto> getCustomerInfo(String name,
			String email, String phoneNumber,String cId,String updatedBy, Pageable pageable)
			throws AccessDeniedException {

	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	CriteriaQuery<CustomerRegisterEntity> query = cb.createQuery(CustomerRegisterEntity.class);
	Root<CustomerRegisterEntity> root = query.from(CustomerRegisterEntity.class);
	List<Predicate> predicates = new ArrayList<>();

	if (name != null && !name.trim().isEmpty()) {
		predicates.add(cb.equal(root.get("name"), name));
	}
	if (email != null && !email.trim().isEmpty()) {
		predicates.add(cb.equal(root.get("email"), email));
	}
	if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
		predicates.add(cb.equal(root.get("phoneNumber"), phoneNumber));
	}
	if (cId != null && !cId.trim().isEmpty()) {
		predicates.add(cb.equal(root.get("cId"), cId));
	}
	if (updatedBy != null && !updatedBy.trim().isEmpty()) {
		predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
	}


	query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	TypedQuery<CustomerRegisterEntity> typedQuery = entityManager.createQuery(query);
	typedQuery.setFirstResult((int) pageable.getOffset());
	typedQuery.setMaxResults(pageable.getPageSize());

	List<CustomerRegisterEntity> entities = typedQuery.getResultList();

	// Map entities to DTOs
	List<CustomerRegistrationInfoDto> dtos = entities.stream().map(entity -> {
		CustomerRegistrationInfoDto dto = new CustomerRegistrationInfoDto();
		dto.setId(entity.getId());
		dto.setCId(entity.getCId());
		dto.setName(entity.getName());
		dto.setEmail(entity.getEmail());
		dto.setPhoneNumber(entity.getPhoneNumber());
		dto.setUserType(entity.getUserType());
		dto.setRegisteredDate(entity.getRegisteredDate());
		dto.setUpdatedDate(entity.getUpdatedDate());
		dto.setRegisterMode(entity.getRegisterMode());
		dto.setUserId(entity.getUpdatedBy());
		return dto;
	}).toList();

	// Count query
	CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	Root<CustomerRegisterEntity> countRoot = countQuery.from(CustomerRegisterEntity.class);
	List<Predicate> countPredicates = new ArrayList<>();

	if (name != null && !name.trim().isEmpty()) {
		countPredicates.add(cb.equal(countRoot.get("name"), name));
	}
	if (email != null && !email.trim().isEmpty()) {
		countPredicates.add(cb.equal(countRoot.get("email"), email));
	}
	if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
		countPredicates.add(cb.equal(countRoot.get("phoneNumber"), phoneNumber));
	}
	if (cId != null && !cId.trim().isEmpty()) {
		countPredicates.add(cb.equal(countRoot.get("cId"), cId));
	}
	if (updatedBy != null && !updatedBy.trim().isEmpty()) {
		countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
	}
	countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	Long total = entityManager.createQuery(countQuery).getSingleResult();

	return new PageImpl<>(dtos, pageable, total);
}
	
	
	public CustomerWalkInUpdateRequest updateCustomer(CustomerWalkInUpdateRequest request) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
	    if (loginStore.isEmpty()) {
	        throw new ResourceNotFoundException("Access denied. This API is restricted to store users only.");
	    }

	    CustomerRegisterEntity customer = registerRepository.findByCId(request.getCustomerId());

	    if (customer == null) {
	        throw new RuntimeException("Customer with cId " + request.getCustomerId() + " not found.");
	    }

	    if (request.getName() != null) customer.setName(request.getName());
	    if (request.getLocation() != null) customer.setLocation(request.getLocation());
	    if (request.getRegisterMode() != null) customer.setRegisterMode(request.getRegisterMode());

	    customer.setUpdatedBy(loginStore.get().getUserId());

	    registerRepository.save(customer);

	    // Prepare response
	    CustomerWalkInUpdateRequest response = new CustomerWalkInUpdateRequest();
	    response.setCustomerId(customer.getCId());
	    response.setName(customer.getName());
	    response.setEmail(customer.getEmail());
	    response.setPhoneNumber(customer.getPhoneNumber());
	    response.setLocation(customer.getLocation());
	    response.setRegisterMode(customer.getRegisterMode());
	    response.setUpdatedBy(loginStore.get().getUserId());
	    return response;
	}


}
