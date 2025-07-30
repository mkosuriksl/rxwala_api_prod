package com.kosuri.stores.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kosuri.stores.config.JwtService;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.dao.Token;
import com.kosuri.stores.dao.TokenRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.request.LoginUserRequest;
import com.kosuri.stores.model.response.LoginUserResponse;
import com.kosuri.stores.model.response.MaterialDetailResponse;
import com.kosuri.stores.model.response.StoreInfoResponse;

@Service
public class StoreUserHandler implements UserDetailsService {
	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private TabStoreRepository tabStoreRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StockRepository stockRepository;

	public LoginUserResponse loginStoreUser(LoginUserRequest request) throws Exception {
		LoginUserResponse response = new LoginUserResponse();
		if ((request.getEmail() == null || request.getEmail().isEmpty())
				&& (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())) {
			throw new APIException("Either email or phone number must be provided, both can't be null/empty");
		}
		TabStoreUserEntity tabStoreUserEntity = repositoryHandler.loginStoreUser(request);
		UserDetails storeUser = loadUserByUsername(tabStoreUserEntity.getStoreUserEmail());

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
		}
		return response;
	}

	public StoreEntity getLoggedInUserStoreInfo(String email) {
		return storeRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found By : " + email));
	}

	public List<StoreInfoResponse> getStoreInfoDetails(String storeName, String businessType, String location) {
		List<StoreEntity> storeList = storeRepository.findByStoreBusinessTypeOrNameOrLocation(businessType, storeName,
				location);
		return storeList.stream().map(store -> {
			StoreInfoResponse storeRes = new StoreInfoResponse();
			storeRes.setId(store.getId());
			storeRes.setName(store.getName());
			storeRes.setEmail(store.getOwnerEmail());
			storeRes.setLocation(store.getLocation());
			storeRes.setPhone(store.getOwnerContact());
			return storeRes;
		}).collect(Collectors.toList());
	}

	/**
	 * @param username the username identifying the user whose data is required.
	 * @return
	 * @throws UsernameNotFoundException
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TabStoreUserEntity customerRegistration = tabStoreRepository.findByStoreUserEmailOrStoreUserContact(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + customerRegistration.getUserType()));

		return User.withUsername(customerRegistration.getStoreUserEmail()).password(customerRegistration.getPassword())
				.authorities(authorities).build();
	}

	public List<MaterialDetailResponse> getMaterialDetail(String storeId, String itemCategory, String itemName) {
		List<StockEntity> materialList = stockRepository.findAllByStoreIdAndItemCategoryOrItemName(storeId,
				itemCategory, itemName);

		return materialList.stream().map(mt -> {
			MaterialDetailResponse mr = new MaterialDetailResponse();
			mr.setStoreId(mt.getStoreId());
			mr.setItemName(mt.getItemName());
			mr.setItemCategory(mt.getItemCategory());
			if (mt.getMrpValue() != null) {
				mr.setMrp(mt.getMrpValue().toString());
			}

			mr.setBrand(mt.getMfName());
			mr.setBatchNumber(mt.getBatch());
			mr.setExpiryDate(mt.getExpiryDate().toString());
			return mr;
		}).toList();
	}

}
