package com.example.SpendSmart.Reposotory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

@Component
public class OTPGeneration {



    @Autowired
    private JavaMailSender mailSender;

    // OTP expiration time (in milliseconds) - 5 minutes
    private static final int OTP_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    // DTO to hold OTP and its expiration timestamp
    public class OTPDetails {
        String otp;
        long timestamp;

        OTPDetails(String otp) {
            this.otp = otp;
            this.timestamp = Instant.now().toEpochMilli();  // Store the timestamp when OTP is generated
        }

        boolean isExpired() {
            long currentTime = Instant.now().toEpochMilli();  // Get current timestamp
            boolean expired = currentTime - timestamp > OTP_EXPIRATION_TIME;
            System.out.println("Checking expiration: " + expired + " (Current Time: " + currentTime + ", OTP Timestamp: " + timestamp + ")");
            return expired;
        }
    }


    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    public void sendOtpEmail(String email, String firstName, String otp) {
        String subject = "Your OTP Code";
        String body = "Dear " + firstName + ",\n\n" +
                "Your OTP code is: " + otp + "\n" +
                "Please enter this code to verify your account.\n\n" +
                "Thank you,";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("faourali009@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    public String verifyOtp(String email, String enteredOtp, Map<String, OTPDetails> otpStorage) {
        OTPDetails otpDetails = otpStorage.get(email);

        if (otpDetails != null) {
            System.out.println("Verifying OTP for email: " + email);
            System.out.println("Stored OTP: " + otpDetails.otp);
            System.out.println("Entered OTP: " + enteredOtp);

            if (otpDetails.isExpired()) {
                System.out.println("OTP expired for email: " + email);
                otpStorage.remove(email);
                return "Expired OTP";
            }

            if (otpDetails.otp.equals(enteredOtp)) {
                otpStorage.remove(email);
                return "Success";
            } else {
                System.out.println("Invalid OTP for email: " + email);
                return "Invalid OTP";
            }
        }
        return "Invalid OTP";
    }

    public void generateAndSendOtp(String email, String firstName, Map<String, OTPDetails> otpStorage) {
        String otp = generateOtp();
        otpStorage.put(email, new OTPDetails(otp));
        sendOtpEmail(email, firstName, otp);
    }
}