package com.kosuri.stores.config;

import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
	@Value("${application.security.jwt.secret-key}")
	private String secretKey;
	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;
	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;

	public static String CURRENT_USER = "";

	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		return Keys
				.hmacShaKeyFor(Decoders.BASE64.decode("SqNPCf7o62C133B686C4B61B8318DE7988BB6pr75CjzVtd6RMCBu3qkY4PSB"));
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		extraClaims.put("userType", getUserTypeFormUserDetails(userDetails));
		return buildToken(extraClaims, userDetails, jwtExpiration, "your-issuer", "your-audience");
	}

	private String getUserTypeFormUserDetails(UserDetails userDetails) {
		if (userDetails instanceof User user) {
			Optional<TabStoreUserEntity> tabStoreUserDetails = null;
			if (user.getUsername().contains("@")) {
				tabStoreUserDetails = tabStoreRepository.findByStoreUserEmail(user.getUsername());
			} else {
				tabStoreUserDetails = tabStoreRepository.findByStoreUserContact(user.getUsername());
			}
			// CURRENT_USER=tabStoreUserDetails.get().getStoreUserEmail();
			if (tabStoreUserDetails.isPresent()) {
				return tabStoreUserDetails.get().getUserType();
			}
		}
		return null;
	}
	public String generateUserToken(UserDetails userDetails) {
		return generateUserToken(new HashMap<>(), userDetails);
	}
	public String generateUserToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		extraClaims.put("userType", getUserTypeFormCustomerDetails(userDetails));
		return buildToken(extraClaims, userDetails, jwtExpiration, "your-issuer", "your-audience");
	}
	private String getUserTypeFormCustomerDetails(UserDetails userDetails) {
		if (userDetails instanceof User user) {
			Optional<CustomerRegisterEntity> customerRegister= null;
			if (user.getUsername().contains("@")) {
				customerRegister = customerRegisterRepository.findByEmail(user.getUsername());
			} else {
				customerRegister = customerRegisterRepository.findByPhoneNumber(user.getUsername());
			}
			// CURRENT_USER=tabStoreUserDetails.get().getStoreUserEmail();
			if (customerRegister.isPresent()) {
				return customerRegister.get().getUserType();
			}
		}
		return null;
	}
	
	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, refreshExpiration, "your-issuer", "your-audience");
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String issuer,
			String audience) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuer(issuer)
				.setAudience(audience).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSignInKey()).compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		CURRENT_USER = userDetails.getUsername();
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

//    public UserDetails getUserDetails(TabStoreUserEntity registration) {
//        Set<GrantedAuthority> authorities = new HashSet<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + registration.getUserType()));
//
//        String encodePassword=bCryptPasswordEncoder.encode(registration.getPassword());
//
//        return new User(
//                registration.getStoreUserEmail(),
//                encodePassword,
//                authorities
//        );
//    }

}
