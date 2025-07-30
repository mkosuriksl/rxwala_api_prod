package com.kosuri.stores.handler;

import com.kosuri.stores.config.AppProperties;

import jakarta.mail.internet.MimeMessage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


public class EmailService {

	private final JavaMailSender javaMailSender;

	private final AppProperties appProperties;
	public EmailService(JavaMailSender javaMailSender, AppProperties appProperties) {
		this.javaMailSender = javaMailSender;
		this.appProperties = appProperties;
	}

	public boolean sendEmailMessage(String email, String mailMessage, String subject) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject(subject);
		message.setFrom(appProperties.getMailUsername());
		message.setText(mailMessage);
		javaMailSender.send(message);
		return true;
	}
	
	 public boolean sendEmailMessagePdf(String recipient, String subject, String htmlContent, byte[] pdfAttachment) {
	        try {
	            MimeMessage message = javaMailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);

	            message.setFrom(appProperties.getMailUsername());
	            helper.setTo(recipient);
	            helper.setSubject(subject);
	            helper.setText(htmlContent, true); // Supports both plain text and HTML
	            
	            if (pdfAttachment != null && pdfAttachment.length > 0) {
	                helper.addAttachment("BookingDetails.pdf", new ByteArrayResource(pdfAttachment));
	            }

	            javaMailSender.send(message);
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();  // Log the error
	            return false;
	        }
	    }

}