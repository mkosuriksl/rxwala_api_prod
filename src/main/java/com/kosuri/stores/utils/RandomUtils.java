package com.kosuri.stores.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

public class RandomUtils {

	private static final Logger logger = LoggerFactory.getLogger(RandomUtils.class);
	
	  public static String generateRandomThreeDigitNumber() {
	        Random random = new Random();
	        int number = random.nextInt(1000); // Generates a number between 0 and 999
	        return String.format("%03d", number); // Pads with leading zeroes if necessary
	    }

	public static String generateOtp(int length, boolean checkDigit) {
		logger.info("generate OTP");
		Random random = new Random();
		int otp = random.nextInt(1000000);
		String profileOtp = ("00000000000" + otp);
		// logger.info("Generated OTP " + profileOtp);

		profileOtp = profileOtp.substring(profileOtp.length() - length);
		if (checkDigit) {
			profileOtp = profileOtp + generateCheckDigit(profileOtp);
		}

		return profileOtp;
	}

	public static String generate10RandomDigit() {
		String numbers = "0123456789";
		Random rndm_method = new Random();
		char[] otp = new char[10];
		for (int i = 0; i < 10; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return new String(otp);
	}

	public static String generate4DigitOTP(int length) {
		String numbers = "0123456789";
		Random rndm_method = new Random();
		char[] otp = new char[length];
		for (int i = 0; i < length; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return new String(otp);
	}

	public static String generateCheckDigit(String pin) {
		logger.info("OTP, {}");

		int digit = 1;
		for (char ch : pin.toCharArray()) {
			logger.info("CHAR, {}");
			if (Character.getNumericValue(ch) == 0) {
				continue;
			}
			digit = digit * Character.getNumericValue(ch);
		}
		logger.info("Check digit, {}");

		String d = String.valueOf(digit);
		while (d.length() > 1) {
			digit = 0;
			for (char ch : d.toCharArray()) {
				digit = digit + Character.getNumericValue(ch);
			}
			d = String.valueOf(digit);
		}
		logger.info("Final check digit, {}", d);
		return d;
	}

	private final static String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

	public static boolean isValidDate(String date) {
		Pattern pattern = Pattern.compile(DATE_PATTERN);
		Matcher matcher = pattern.matcher(date);
		return matcher.matches();
	}

	public String generateRandomNumber() {
		Random random = new Random();
		int randomNumber = random.nextInt(900000) + 100000; // Generates a 6-digit random number
		return String.valueOf(randomNumber);
	}

}
