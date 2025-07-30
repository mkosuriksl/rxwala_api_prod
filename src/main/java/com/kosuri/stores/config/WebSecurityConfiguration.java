package com.kosuri.stores.config;

import com.kosuri.stores.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.http.HttpHeaders;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	@Autowired
	private final UserHandler userHandler;

	@Autowired
	private final DataSource dataSource;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	public static final String[] PUBLIC_URLS = { "/user/**", "/admin/login", "/store-user/login", "/v3/api-docs",
			"/dc-packages/home-search-diagnostic","/store/getStoreLocation",
			"/primaryCare/service-WithoutSec","/admin/register","/home-search-medicine",
			"/customerRegister","/rxwala-send-otp-verify-email","/rxwala-verify-otp-verify-email",
			"/rxwala-login-customerlogin","/sendmobileOtp","/verifySmsOtp","/customerforgetPassword/sendOtp",
			"/customer/forgetPassword/verification","/get_dc_service_category_home","/primaryCare/get_pc_service_category_home", "/primaryCare/get_pc_service_category_home-by-search","/dc-packages/getHomeSearch-PackageDetails",
			"/get_dc_service_category_home-by-search","/sale/report/get-customer-sale-invoice-by-search",
			"/rxwala/get-hcdistinctServiceCategory","/store/get-location-by-search","/stock/get-itemName-by-search",
			"/dc-packages/home-search-primarycare","/v2/api-docs", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**", "/api/v1/auth/**", "/v2/api-docs",
			"/v3/api-docs", "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
			"/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html" };

	@Autowired
	public WebSecurityConfiguration(UserHandler userHandler, DataSource dataSource) {
		this.userHandler = userHandler;
		this.dataSource = dataSource;

	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeRequests(authorizeRequests -> authorizeRequests.requestMatchers(PUBLIC_URLS).permitAll()
						.anyRequest().authenticated())
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(customerDaoAuthenticationProvider());

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

	@Bean
	public DaoAuthenticationProvider customerDaoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userHandler);
		// provider.setPasswordEncoder(bCryptPasswordEncoder);
		return provider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		JdbcDaoImpl userDetailsService = new JdbcDaoImpl();
		userDetailsService.setDataSource(dataSource);
		return userDetailsService;
	}
}