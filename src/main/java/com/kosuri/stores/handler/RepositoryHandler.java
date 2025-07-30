package com.kosuri.stores.handler;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.kosuri.stores.constant.StoreConstants;
import com.kosuri.stores.dao.AdminStoreBusinessTypeEntity;
import com.kosuri.stores.dao.AdminStoreBusinessTypeRepository;
import com.kosuri.stores.dao.AdminStoreCategoryEntity;
import com.kosuri.stores.dao.AdminStoreCategoryRepository;
import com.kosuri.stores.dao.AdminStoreMembershipEntity;
import com.kosuri.stores.dao.AdminStoreMembershipRepository;
import com.kosuri.stores.dao.AdminStoreVerificationEntity;
import com.kosuri.stores.dao.AdminStoreVerificationRepository;
import com.kosuri.stores.dao.AmbulanceBookingDetailEntity;
import com.kosuri.stores.dao.AmbulanceBookingDetailRepository;
import com.kosuri.stores.dao.AmbulanceMasterEntity;
import com.kosuri.stores.dao.AmbulanceMasterRepository;
import com.kosuri.stores.dao.AmbulancePriceEntity;
import com.kosuri.stores.dao.AmbulancePriceRepository;
import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.DiagnosticMasterRepository;
import com.kosuri.stores.dao.DiagnosticServiceRepository;
import com.kosuri.stores.dao.DiagnosticServicesEntity;
import com.kosuri.stores.dao.MembershipDetailsEntity;
import com.kosuri.stores.dao.MembershipDetailsRepository;
import com.kosuri.stores.dao.MembershipHdrEntity;
import com.kosuri.stores.dao.MembershipHdrRepository;
import com.kosuri.stores.dao.PharmacistEntity;
import com.kosuri.stores.dao.PharmacistRepository;
import com.kosuri.stores.dao.PrimaryCareCenterRepository;
import com.kosuri.stores.dao.PrimaryCareEntity;
import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseRepository;
import com.kosuri.stores.dao.RetailerEntity;
import com.kosuri.stores.dao.RetailerRepository;
import com.kosuri.stores.dao.RoleEntity;
import com.kosuri.stores.dao.RoleRepository;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.SaleRepository;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.dao.UserOTPEntity;
import com.kosuri.stores.dao.UserOTPRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.Status;
import com.kosuri.stores.model.enums.UserType;
import com.kosuri.stores.model.request.AddTabStoreUserRequest;
import com.kosuri.stores.model.request.AddUserRequest;
import com.kosuri.stores.model.request.CreateStoreRequest;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.request.OTPRequest;
import com.kosuri.stores.model.request.PharmasistRequest;
import com.kosuri.stores.model.request.PrimaryCareUserRequest;
import com.kosuri.stores.model.request.VerifyOTPRequest;
import com.kosuri.stores.model.response.RenewalStoreMemberships;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Service
public class RepositoryHandler {

	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private UserOTPRepository userOTPRepository;

	@Autowired
	private DiagnosticServiceRepository diagnosticServiceRepository;

	@Autowired
	private DiagnosticMasterRepository diagnosticMasterRepository;
	@Autowired
	private OtpHandler otpHandler;
	@Autowired
	private PrimaryCareCenterRepository primaryCareCenterRepository;

	@Autowired
	private PharmacistRepository pharmacistRepository;

	@Autowired
	private AdminStoreMembershipRepository adminStoreMembershipRepository;

	@Autowired
	private AdminStoreVerificationRepository adminStoreVerificationRepository;

	@Autowired
	private AdminStoreBusinessTypeRepository adminStoreBusinessTypeRepository;

	@Autowired
	private AdminStoreCategoryRepository adminStoreCategoryRepository;

	@Autowired
	private AmbulanceBookingDetailRepository ambulanceBookingRepository;

	@Autowired
	private AmbulanceMasterRepository ambulanceMasterRepository;

	@Autowired
	private AmbulancePriceRepository ambulancePriceRepository;
	@Autowired
	private RetailerRepository retailerRepository;

	@Autowired
	private MembershipHdrRepository membershipHdrRepository;

	@Autowired
	private MembershipDetailsRepository membershipDetailsRepository;

	@Autowired
	private CustomerRegisterRepository registerRepository;

	public StoreEntity addStoreToRepository(StoreEntity storeEntity) throws Exception {
		Optional<UserOTPEntity> userOTPEntityOptional = userOTPRepository
				.findByUserEmailAndActive(storeEntity.getOwnerEmail(), 1);
		if (userOTPEntityOptional.isEmpty()) {
			throw new APIException("Store User Not Found ");
		}
		try {
	        return storeRepository.save(storeEntity);
	    } catch (DataIntegrityViolationException ex) {
	        throw new APIException(
	            storeEntity.getUserId() + "_" + storeEntity.getId() + "userIdstoreId already exists. Please use a unique userIdstoreId."
	        );
	    }
	}

	public StoreEntity updateStore(StoreEntity storeEntity) throws Exception {
		Optional<StoreEntity> store = storeRepository.findById(storeEntity.getId());

		if (store.isEmpty()) {
			System.out.println("Entity not found");
			throw new APIException("Store with this id not found!");
		}

		return storeRepository.save(storeEntity);
	}

	public void addUser(StoreEntity storeEntity, AddUserRequest request) throws Exception {

		Optional<RoleEntity> role = roleRepository.findByRoleName(request.getRole());
		if (!role.isPresent()) {
			throw new APIException("Role does not exist. Please enter a valid role");
		}
		storeRepository.save(storeEntity);

	}

	public List<StoreEntity> getAllStores() throws Exception {
		List<StoreEntity> storeEntities = storeRepository.findAll();
		return storeEntities;
	}

	public Optional<List<PurchaseEntity>> getPurchaseRecordsByStore(String storeId) {
		return purchaseRepository.findByStoreId(storeId);
	}

	public Optional<List<SaleEntity>> getSaleRecordsByStore(String storeId) {
		return saleRepository.findByStoreId(storeId);
	}

	public Optional<List<StockEntity>> getStockRecordsByStore(String storeId) {
		return stockRepository.findByStoreId(storeId);
	}

	public boolean validateUser(AddUserRequest request) throws Exception {
		Optional<List<StoreEntity>> existingStores = storeRepository.findByOwnerEmailOrOwnerContact(request.getEmail(),
				request.getPhoneNumber());
		if (existingStores.isEmpty()) {
			return true;
		}
		for (StoreEntity store : existingStores.get()) {
			// TODO Update to query based on id
			if (store.getId().contains("DUMMY")) {
				System.out.println("User already exists in system");
				throw new APIException("User already exists in system");
			}
		}

		return true;
	}

	public boolean addStoreUser(TabStoreUserEntity storeEntity, AddTabStoreUserRequest request) throws Exception {

		Optional<RoleEntity> role = roleRepository.findByRoleName(request.getRole());

		TabStoreUserEntity user = tabStoreRepository.save(storeEntity);
		if (user.getStoreUserEmail() != null) {
			// sets the EmailId and created Date for UserOTPEnitity
			UserOTPEntity userOtp = new UserOTPEntity();
			userOtp.setUserOtpId(storeEntity.getUserId());
			userOtp.setUserEmail(request.getUserEmail());
			userOtp.setActive(0);
			userOtp.setCreatedOn(LocalDateTime.now().toString());
			userOtp.setUserPhoneNumber(storeEntity.getStoreUserContact());
			userOTPRepository.save(userOtp);
			OTPRequest otpRequest = createOTPRequest(storeEntity.getStoreUserEmail(),
					storeEntity.getStoreUserContact());
			boolean isMessageSent = sendEmailOtp(otpRequest);
			boolean isPhoneOtpSent = sendOtpToSMS(otpRequest);
			return (isMessageSent || isPhoneOtpSent);
		}
		return false;
	}

	private OTPRequest createOTPRequest(String storeUserEmail, String storeUserContact) {
		OTPRequest otpRequest = new OTPRequest();
		otpRequest.setEmail(storeUserEmail);
		otpRequest.setPhoneNumber(storeUserContact);
		otpRequest.setIsForgetPassword(false);
		return otpRequest;
	}

	public TabStoreUserEntity loginUser(LoginUserRequest request) throws Exception {

		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
				.findByStoreUserEmailOrStoreUserContact(request.getEmail(), request.getPhoneNumber());
		if (tabStoreUserEntityOptional.isEmpty()) {
			throw new APIException("Invalid Credentials!");
		}
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.orElse(null);
		if (tabStoreUserEntity.getPassword() != null
				&& isValidPassword(request.getPassword(), tabStoreUserEntity.getPassword())) {
			return tabStoreUserEntity;
		}

		throw new APIException("Invalid Credentials!");
	}

	public TabStoreUserEntity loginStoreUser(LoginUserRequest request) throws Exception {

		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
				.findByStoreUserEmailOrStoreUserContact(request.getEmail(), request.getPhoneNumber());
		if (tabStoreUserEntityOptional.isEmpty()) {
			throw new APIException("Invalid Credentials!");
		}
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.orElse(null);
		if (tabStoreUserEntity.getPassword() != null
				&& isValidPassword(request.getPassword(), tabStoreUserEntity.getPassword())) {
			return tabStoreUserEntity;
		}

		throw new APIException("Invalid Credentials!");
	}

	//CUSTOMER LOGIN
	public CustomerRegisterEntity loginCustomerUser(LoginUserRequest request) throws Exception {
		Optional<CustomerRegisterEntity> tabStoreUserEntityOptional = registerRepository
				.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber());
		if (tabStoreUserEntityOptional.isEmpty()) {
			throw new APIException("Invalid Credentials!");
		}
		CustomerRegisterEntity tabStoreUserEntity = tabStoreUserEntityOptional.orElse(null);
		if (tabStoreUserEntity.getPassword() != null
				&& isValidPassword(request.getPassword(), tabStoreUserEntity.getPassword())) {
			return tabStoreUserEntity;
		}

		throw new APIException("Invalid Credentials!");
	}

	private boolean isValidPassword(String password, String encryptedPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), encryptedPassword);
		return result.verified;
	}

	public boolean validateStoreUser(AddTabStoreUserRequest request) throws Exception {
		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
				.findByStoreUserEmailOrStoreUserContact(request.getUserEmail(), request.getUserPhoneNumber());
		if (tabStoreUserEntityOptional.isEmpty()) {
			return true;
		}
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.orElse(null);
		if (tabStoreUserEntity.getStoreUserEmail().contains(request.getUserEmail())
				|| tabStoreUserEntity.getStoreUserContact().contains(request.getUserPhoneNumber())) {
			System.out.println("User already exists in system");
			throw new APIException("User already exists in system");
		}
		boolean isStoreAdmin = storeAdminCheck(request.getUserType());
		if (!isStoreAdmin && (request.getStoreAdminEmail() == null || request.getStoreAdminEmail().isEmpty())
				&& (request.getStoreAdminMobile() == null || request.getStoreAdminMobile().isEmpty())) {
			throw new APIException("Store Admin Email or Mobile Should be Present For Store User");
		}
		return true;
	}

	private boolean storeAdminCheck(String userType) {
		return userType.equalsIgnoreCase(UserType.SA.toString());
	}

	public boolean verifyEmailOtp(VerifyOTPRequest verifyOTPRequest) throws APIException {
		String email = verifyOTPRequest.getEmail();
		String emailOtp = verifyOTPRequest.getOtp();

		UserOTPEntity userOtpEntity = userOTPRepository.findByUserEmailAndEmailOtpOrForgetEmailOtp(email, emailOtp);
		if (userOtpEntity != null) {
			if (verifyOTPRequest.getIsForgetPassword()) {
				return userOtpEntity.getActive() != null && userOtpEntity.getActive() == 1;
			} else {
				if (userOtpEntity.isEmailVerify() || userOtpEntity.getActive() == 1) {
					StoreConstants.IS_EMAIL_ALREADY_VERIFIED = true;
				}
				if (!userOtpEntity.isEmailVerify()) {
					userOtpEntity.setEmailVerify(true);
					userOtpEntity.setActive(1);
					userOtpEntity.setUpdatedOn(LocalTime.now().toString());
					userOTPRepository.save(userOtpEntity);
					Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
							.findByStoreUserEmail(email);
					TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.get();
					tabStoreUserEntity.setStatus(Status.ACTIVE.name());
					tabStoreRepository.save(tabStoreUserEntity);
					return true;
				}
			}
		} else {
			throw new APIException("Invalid Otp");
		}
		return false;
	}

	@Transactional
	public boolean verifyPhoneOtp(VerifyOTPRequest verifyOTPRequest) {
		String phoneOtp = verifyOTPRequest.getOtp();
		String phoneNumber = verifyOTPRequest.getPhoneNumber();
		UserOTPEntity userOtpEntity = userOTPRepository.findByUserPhoneNumberAndPhoneOtpOrForgetEmailOtp(phoneNumber,
				phoneOtp);
		if (userOtpEntity == null) {
			throw new RuntimeException("Something went wrong. Please check the OTP.");
		}

		if (userOtpEntity != null) {
			if (verifyOTPRequest.getIsForgetPassword()) {
				// if
				userOtpEntity.setPhoneOtp(null);
				return userOtpEntity.getActive() != null && userOtpEntity.getActive() == 1;
			} else {
				if (userOtpEntity.getActive() == 1) {
					StoreConstants.IS_EMAIL_ALREADY_VERIFIED = true;
				}
				userOtpEntity.setSmsVerify(true);
				userOtpEntity.setActive(1);
				userOtpEntity.setUpdatedOn(LocalTime.now().toString());
				userOtpEntity.setPhoneOtp(null);
				userOTPRepository.save(userOtpEntity);
				Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
						.findByStoreUserContact(phoneNumber);
				TabStoreUserEntity tabStoreUserEntity = tabStoreUserEntityOptional.get();
				tabStoreUserEntity.setStatus(Status.ACTIVE.name());
				tabStoreRepository.save(tabStoreUserEntity);
				return true;
			}
		}
		return false;
	}

	public boolean sendEmailOtp(OTPRequest request) {
		Optional<TabStoreUserEntity> tabStoreUserOptional = tabStoreRepository.findByStoreUserEmail(request.getEmail());
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserOptional.orElse(null);
		if (null != tabStoreUserEntity && null != tabStoreUserEntity.getUserType()
				&& tabStoreUserEntity.getUserType().equalsIgnoreCase(UserType.SA.toString())) {
			String storeUserEmail = request.getEmail();
			return (request.getIsForgetPassword() ? otpHandler.sendOtpToEmail(storeUserEmail, true, false, false)
					: otpHandler.sendOtpToEmail(storeUserEmail, false, false, false));
		}
		return false;
	}

	public boolean sendOtpToSMS(OTPRequest request) {
		Optional<TabStoreUserEntity> tabStoreUserOptional = tabStoreRepository
				.findByStoreUserContact(request.getPhoneNumber());
		TabStoreUserEntity tabStoreUserEntity = tabStoreUserOptional.orElse(null);
		if (null != tabStoreUserEntity && null != tabStoreUserEntity.getUserType()
				&& tabStoreUserEntity.getUserType().equalsIgnoreCase(UserType.SA.toString())) {
			String storeUserPhoneNumber = request.getPhoneNumber();
			return otpHandler.sendOtpToPhoneNumber(storeUserPhoneNumber);
		}
		return false;
	}

	public boolean addDiagnosticCenter(DiagnosticServicesEntity diagnosticServicesEntity) throws APIException {
		Optional<DiagnosticServicesEntity> diagnosticServicesEntityOptional = diagnosticServiceRepository
				.findById(diagnosticServicesEntity.getUserServiceId());
		if (diagnosticServicesEntityOptional.isEmpty()) {
			DiagnosticServicesEntity dcEntity = diagnosticServiceRepository.save(diagnosticServicesEntity);
			return (!ObjectUtils.isEmpty(dcEntity));
		} else {
			throw new APIException("Diagnostic Service Already Present." + diagnosticServicesEntity.getServiceId());
		}

	}

	public boolean isDCActive(String userId, String storeId) throws APIException {
		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository.findById(userId);
		if (tabStoreUserEntityOptional.isEmpty()) {
			throw new APIException("User Id Does not exists");
		}

		Optional<StoreEntity> storeInfoOptional = storeRepository.findById(storeId);
		if (storeInfoOptional.isPresent()) {
			StoreEntity storeEntity = storeInfoOptional.get();
			return storeEntity.getStatus().equalsIgnoreCase(Status.ACTIVE.toString());
		}

		return false;
	}

//	public boolean isDCActive(DiagnosticCenterRequestDto request) throws APIException {
//		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository.findById(request.getUserId());
//		if (tabStoreUserEntityOptional.isEmpty()) {
//			throw new APIException("User Id Does not exists");
//		}
//
//		Optional<StoreEntity> storeInfoOptional = storeRepository.findById(request.getStoreId());
//		if (storeInfoOptional.isPresent()) {
//			StoreEntity storeEntity = storeInfoOptional.get();
//			return storeEntity.getStatus().equalsIgnoreCase(Status.ACTIVE.toString());
//		}
//
//		return false;
//	}

	public DiagnosticServicesEntity findServiceById(String userServiceId) {
		Optional<DiagnosticServicesEntity> diagnosticServicesEntityOptional = diagnosticServiceRepository
				.findById(userServiceId);
		return diagnosticServicesEntityOptional.orElse(null);
	}

	public void saveDiagnosticServiceEntity(DiagnosticServicesEntity serviceEntity) {
		diagnosticServiceRepository.save(serviceEntity);
	}

	public boolean isPCActive(PrimaryCareUserRequest request) {
		StoreEntity storeEntity = storeRepository.findById(request.getStoreId())
				.orElseThrow(() -> new RuntimeException("Store Not Found By Id : " + request.getStoreId()));
		return (Objects.requireNonNull(storeEntity).getStatus().equalsIgnoreCase(Status.ACTIVE.name()));

	}

	public boolean isPCActive(String storeId) {
		StoreEntity storeEntity = storeRepository.findById(storeId)
				.orElseThrow(() -> new RuntimeException("Store Not Found By Id : " + storeId));
		return (Objects.requireNonNull(storeEntity).getStatus().equalsIgnoreCase(Status.ACTIVE.name()));

	}

	public boolean addPrimaryCareCenter(PrimaryCareEntity primaryCareEntity) throws APIException {
		Optional<PrimaryCareEntity> primaryCareEntityOptional = primaryCareCenterRepository
				.findById(primaryCareEntity.getUserIdStoreIdServiceId());
		if (primaryCareEntityOptional.isEmpty()) {
			PrimaryCareEntity primaryCare = primaryCareCenterRepository.save(primaryCareEntity);
			return (!ObjectUtils.isEmpty(primaryCare));
		} else {
			throw new APIException("Primary Care Already Present." + primaryCareEntity.getServiceId());
		}
	}

	public PrimaryCareEntity findPrimaryServiceById(String userServiceId) {
		Optional<PrimaryCareEntity> primaryCareEntityOptional = primaryCareCenterRepository.findById(userServiceId);
		return primaryCareEntityOptional.orElse(null);
	}

	public void savePrimaryServiceEntity(PrimaryCareEntity serviceEntity) {
		primaryCareCenterRepository.save(serviceEntity);
	}

	public boolean updatePassword(TabStoreUserEntity tabStoreUserEntity) {
		TabStoreUserEntity tabStoreUserResponse = tabStoreRepository.save(tabStoreUserEntity);
		return (!ObjectUtils.isEmpty(tabStoreUserResponse));
	}

	public TabStoreUserEntity getTabStoreUser(String emailAddress, String userContactNumber) {
		Optional<TabStoreUserEntity> tabStoreUserEntityOptional = tabStoreRepository
				.findByStoreUserEmailOrStoreUserContact(emailAddress, userContactNumber);
		return tabStoreUserEntityOptional.orElse(null);
	}

	public boolean addPharmacist(PharmacistEntity entity) {
		try {
			pharmacistRepository.save(entity);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean validatePharmacist(PharmasistRequest request) throws Exception {

		Optional<PharmacistEntity> pharmacistEntityOptional = pharmacistRepository
				.findByPharmacistEmailAddressOrPharmacistContact(request.getPharmaUserEmail(),
						request.getPharmaUserContact());
		if (pharmacistEntityOptional.isEmpty()) {
			return true;
		}
		PharmacistEntity pharmacistEntity = pharmacistEntityOptional.orElse(null);
		if (pharmacistEntity.getPharmacistEmailAddress().contains(request.getPharmaUserEmail())
				|| pharmacistEntity.getPharmacistContact().contains(request.getPharmaUserContact())) {
			throw new APIException("Pharmacist already exists in system");
		}
		return true;
	}

	public List<PharmacistEntity> findPharmacist(String mobileNumber, String emailAddress, String availableLocation) {
		return pharmacistRepository.findByPharmacistEmailAddressOrPharmacistContactOrPharmacistAvailableLocation(
				emailAddress, mobileNumber, availableLocation);
	}

	public boolean findPharmacistBasedOnContactNumberOrEmailAddress(String pharmaUserContact, String pharmaUserEmail) {
		Optional<PharmacistEntity> pharmacistEntityOptional = pharmacistRepository
				.findByPharmacistEmailAddressOrPharmacistContact(pharmaUserEmail, pharmaUserContact);
		PharmacistEntity pharmacistEntity = pharmacistEntityOptional.orElse(null);
		return (pharmacistEntity != null);
	}

	public boolean updatePharmacist(PharmacistEntity entity) {
		try {
			pharmacistRepository.save(entity);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isStorePresent(CreateStoreRequest request) {
		Optional<StoreEntity> storeEntity = storeRepository.findById(request.getId());
		return storeEntity.isPresent();
	}

	public boolean isStorePresent(String userIdStoreId) {
		Optional<StoreEntity> storeEntity = storeRepository.findByUserIdStoreId(userIdStoreId);
		return storeEntity.isPresent();
	}

	public boolean isOwnerPresent(String ownerEmail, String ownerContact) {
		Optional<TabStoreUserEntity> storeUserEntity = tabStoreRepository
				.findByStoreUserEmailOrStoreUserContact(ownerEmail, ownerContact);
		return storeUserEntity.isPresent();
	}

	public AdminStoreMembershipEntity getStoreVerificationDetails(String storeCategory) {
		Optional<AdminStoreMembershipEntity> adminStoreMembershipEntity = adminStoreMembershipRepository
				.findByPlanIdAndStoreCategory(StoreConstants.PLANID, storeCategory);
		return adminStoreMembershipEntity.orElse(null);
	}

	public AdminStoreVerificationEntity getAdminStoreVerification(String userIdstoreId) {
		Optional<AdminStoreVerificationEntity> adminStoreVerificationEntity = adminStoreVerificationRepository
				.findByUserIdStoreId(userIdstoreId);
		return adminStoreVerificationEntity.orElse(null);
	}

	public AdminStoreVerificationEntity saveAdminStoreVerificationEntity(AdminStoreVerificationEntity entity) {
		return adminStoreVerificationRepository.save(entity);
	}

	public AdminStoreBusinessTypeEntity getStoreBusinessTypeByName(String businessType) {
		Optional<AdminStoreBusinessTypeEntity> adminStoreBusinessTypeEntity = adminStoreBusinessTypeRepository
				.findByBusinessName(businessType);
		return adminStoreBusinessTypeEntity.orElse(null);
	}

	public List<AdminStoreBusinessTypeEntity> getAllAdminStoreBusinessTypes() {
		return adminStoreBusinessTypeRepository.findAll();
	}

	public List<AdminStoreCategoryEntity> getAllAdminStoreCategories() {
		return adminStoreCategoryRepository.findAll();
	}

	public List<StockEntity> getStockRecordsByBusinessType(String storeId, String businessType) {
		Optional<StoreEntity> storeOptional = storeRepository.findByIdAndStoreBusinessType(storeId, businessType);
		if (storeOptional.isPresent()) {
			Optional<List<StockEntity>> stockListOptional = stockRepository.findByStoreId(storeId);
			return stockListOptional.orElse(Collections.emptyList());
		} else {
			return Collections.emptyList();
		}
	}

	public boolean addAmbulanceBooking(AmbulanceBookingDetailEntity ambulanceBooking) {
		try {
			ambulanceBookingRepository.save(ambulanceBooking);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addAmbulanceMaster(AmbulanceMasterEntity ambulanceMaster) {
		try {
			ambulanceMasterRepository.save(ambulanceMaster);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addAmbulancePrice(AmbulancePriceEntity ambulancePrice) {
		try {
			ambulancePriceRepository.save(ambulancePrice);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<RetailerEntity> getAllRetailers() {
		return retailerRepository.findAll();
	}

	public List<AdminStoreMembershipEntity> getAllAdminStoreMembership() {
		return adminStoreMembershipRepository.findByPlanIdNot("Plan000");
	}

	public MembershipHdrEntity addMembershipHdr(MembershipHdrEntity membershipHdrEntity) {
		return membershipHdrRepository.save(membershipHdrEntity);
	}

	public MembershipDetailsEntity addMembershipDetails(MembershipDetailsEntity membershipDetailsEntity) {
		return membershipDetailsRepository.save(membershipDetailsEntity);
	}

	public Optional<MembershipHdrEntity> findByOrderId(String orderId) {
		return membershipHdrRepository.findByOrderId(orderId);
	}

	public Optional<MembershipDetailsEntity> findMembershipDetailsByOrderId(MembershipHdrEntity membershipHdrEntity) {
		return membershipDetailsRepository.findByOrderId(membershipHdrEntity);
	}

	public List<RenewalStoreMemberships> findAllFromMembershipDetails(Pageable pageable, String planId, String storeId,
			String orderId, String noOfDays, Boolean status) {
		return membershipDetailsRepository.findAllFields(pageable, planId, storeId, orderId, noOfDays, status);
	}

}