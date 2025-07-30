package com.kosuri.stores.handler;


import com.kosuri.stores.dao.*;
import com.kosuri.stores.template.EmailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OtpHandler {


    private final EmailService emailService;

    private final SmsHandler smsHandler;
    @Autowired
    public OtpHandler(EmailService emailService, SmsHandler smsHandler) {
        this.emailService = emailService;
        this.smsHandler = smsHandler;
    }

    @Autowired
    private UserOTPRepository userOtpRepository;
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private CustomerRegisterRepository customerRegisterRepository;
    
	private final Map<String, String> otpStorage = new HashMap<>(); 


    public boolean sendOtpToEmail(String email, Boolean isForgetPassword, boolean isNotification, boolean isUpdateStore) {
        if (isNotification || isUpdateStore){
            Optional<List<StoreEntity>> storeDetails = storeRepository.findByOwnerEmail(email);
            String location = getLocationDetails(storeDetails);
            String message = createNotificationTemplate(email, isUpdateStore, location);
            return emailService.sendEmailMessage(email, message, "Notification From RxKolan");
        } else {
            Optional<UserOTPEntity> userOtpOptional = userOtpRepository.findByUserEmail(email);
            String otp = OtpHandler.generateOTP(true);
            boolean messageSent;
            EmailTemplate template = new EmailTemplate("static/send-otp.html");
            Map<String, String> replacements = new HashMap<>();

            replacements.put("user", email);
            replacements.put("otp", otp);
            String message = template.getTemplate(replacements);
            messageSent =  emailService.sendEmailMessage(email, message, "OTP For RxWala");
            if (messageSent) {
                UserOTPEntity userOtp = new UserOTPEntity();
                if (userOtpOptional.isPresent()) {
                    userOtp = userOtpOptional.get();
                    if (isForgetPassword){
                        userOtp.setForgetPasswordOtp(otp);
                    }else{
                        userOtp.setEmailOtp(otp);
                    }
                    userOtp.setEmailOtpDate(new Date());
                }
                userOtpRepository.save(userOtp);
            }
            return messageSent;
        }
    }

    private  String createNotificationTemplate(String email, boolean isUpdateStore, String location) {
        EmailTemplate template = new EmailTemplate();
        Map<String, String> replacements = new HashMap<>();
        if(isUpdateStore){
            template = new EmailTemplate("static/send-update-notification.html");
            replacements.put("user", email);
            replacements.put("location", location);
        } else {
            template = new EmailTemplate("static/send-notification.html");
            replacements.put("user", email);
            replacements.put("location", location);
        }

        return template.getTemplate(replacements);
    }


    private String getLocationDetails(Optional<List<StoreEntity>> storeDetails) {

        if (storeDetails.isPresent()) {
            for (StoreEntity store : storeDetails.get()) {
                if (store.getLocation() != null && !store.getLocation().isEmpty()) {
                    return store.getLocation();
                }
            }
        }
        return null;
    }

    public boolean sendOtpToPhoneNumber(String phoneNumber) {

        Optional<UserOTPEntity> userOtpOptional = userOtpRepository.findByUserPhoneNumber(phoneNumber);
        String otp = OtpHandler.generateOTP(true);
        boolean messageSent = smsHandler.sendSMSMessage(phoneNumber,otp);
        if (messageSent) {
            if (userOtpOptional.isPresent()) {
                UserOTPEntity userOtp = userOtpOptional.get();
                userOtp.setPhoneOtp(otp);
                userOtp.setPhoneOtpDate(new Date());
                userOtpRepository.save(userOtp);
            }
        }
        return messageSent;
    }


    public static String generateOTP(boolean isOTP) {
        Random random = new Random();
        int lowerBound = isOTP ? 100000 : 1000;
        return String.valueOf(lowerBound + random.nextInt(999999 - lowerBound + 1));
    }
    
    public String generateOtp(String mail,String operation) {
    	 int randomNum = (int) (Math.random() * 900000) + 100000;
    	    String otp = String.valueOf(randomNum);
//    	    otpStorage.put(mail, otp);

    	    Optional<CustomerRegisterEntity> customerOpt = customerRegisterRepository.findByEmail(mail);
    	    
    	    if (customerOpt.isPresent()) {
    	        CustomerRegisterEntity customer = customerOpt.get();

    	        // Save OTP in the correct field based on operation
    	        if ("registration".equals(operation)|| "forgotPassword".equals(operation)) {
    	            customer.setEmailOtp(otp);
    	        } else if ("mobile_verification".equals(operation)) {
    	            customer.setMobileOtp(otp);
    	        }

    	        customerRegisterRepository.save(customer); // Update record in DB
    	    }
    	    // Use Email Template
    	    EmailTemplate template = new EmailTemplate("static/send-otp.html");
    	    Map<String, String> replacements = new HashMap<>();
    	    replacements.put("user", mail);
    	    replacements.put("otp", otp);

    	    String message = template.getTemplate(replacements);

    	    emailService.sendEmailMessage(mail, message, "OTP For RxWala");
    	    return otp;
    }
    
    public String generateMobileOtp(String mobile) {
		int randomNum = (int) (Math.random() * 900000) + 100000;
		String otp = String.valueOf(randomNum);
//		otpStorage.put(mobile, otp);
		Optional<CustomerRegisterEntity> customerOpt = customerRegisterRepository.findByPhoneNumber(mobile);

	    if (customerOpt.isPresent()) {
	        CustomerRegisterEntity customer = customerOpt.get();
	        customer.setMobileOtp(otp); // Save OTP in DB
	        customerRegisterRepository.save(customer);
	    }
		smsHandler.sendSMSMessage(mobile, otp);
		return otp;
	}

    public boolean verifyOtp(String email, String enteredOtp) {
//        String storedOtp = otpStorage.get(email);
//        return storedOtp != null && storedOtp.equals(enteredOtp);
    	 Optional<CustomerRegisterEntity> customerOpt = customerRegisterRepository.findByEmail(email);

    	    if (customerOpt.isPresent()) {
    	        CustomerRegisterEntity customer = customerOpt.get();
    	        String storedOtp = customer.getEmailOtp(); // Get OTP from DB

    	        return storedOtp != null && storedOtp.equals(enteredOtp);
    	    }
    	    return false;
    }

    public boolean verifyMobileOtp(String mobile, String enteredOtp) {
//		String storedOtp = otpStorage.get(mobile);
//		return storedOtp != null && storedOtp.equals(enteredOtp);
    	Optional<CustomerRegisterEntity> customerOpt = customerRegisterRepository.findByPhoneNumber(mobile);

	    if (customerOpt.isPresent()) {
	        CustomerRegisterEntity customer = customerOpt.get();
	        String storedOtp = customer.getMobileOtp(); // Get OTP from DB

	        return storedOtp != null && storedOtp.equals(enteredOtp);
	    }
	    return false;
	}
}
