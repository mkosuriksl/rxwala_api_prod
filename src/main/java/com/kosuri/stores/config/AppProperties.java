package com.kosuri.stores.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class AppProperties {

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    private boolean mailSmtpAuth=true;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean mailSmtpStartTlsEnable;

    @Value("${spring.web.resources.static-locations}")
    private String staticLocations;

    @Value("${rxkolan.textLocal.apiKey}")
    private String apiKey;

    @Value("${rxkolan.textLocal.sender}")
    private String sender;

    @Value("${rxkolan.textLocal.url}")
    private String url;

    @Value("${rxkolan.otp.sms-message}")
    private String smsMessage;

}
