package com.example.SpendSmart.RestController;

import com.example.SpendSmart.DTA.Response;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.OTPGeneration;
import com.example.SpendSmart.Reposotory.UserRegisterRepostray;
import com.example.SpendSmart.Service.TokenService;
import com.example.SpendSmart.Service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/spendSmart")
public class UserRegsiterController {

    @Autowired
    private UserRegisterService userregisterservice;
    @Autowired
    private TokenService tokenservice;
    @Autowired
    private OTPGeneration otpGeneration;
    @Autowired
    private UserRegisterRepostray userRegisterRepostray;

    // In-memory map to store OTP details temporarily
    private Map<String, OTPGeneration.OTPDetails> otpStorage = new ConcurrentHashMap<>();


    @GetMapping("/user/{id}")
    public ResponseEntity<RegiUser> getUserInfo(@PathVariable int id) {
        Optional<RegiUser> regiUserOptional = userregisterservice.getUserInfoById(id);

        // Check if the user exists and return it
        if (regiUserOptional.isPresent()) {
            return ResponseEntity.ok(regiUserOptional.get());
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if user is not found
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@RequestBody RegiUser request) {
        int id = userregisterservice.Register(request);
        if(id!=-1){
        String token=tokenservice.generateToken(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Response("Success", HttpStatus.OK.value(), id,token));
        } else {
            return  ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new Response("Email Taken", HttpStatus.CONFLICT.value(), -1,"NA"));

        }
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String firstName = requestData.get("firstName");

        if (email == null || firstName == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and firstName are required.");
        }
        String normalizedTitln = email.toLowerCase();
        Optional<RegiUser> num=userRegisterRepostray.findByEmail(normalizedTitln);
        if(num.isEmpty()) {
            otpGeneration.generateAndSendOtp(email, firstName, otpStorage);
            return ResponseEntity.ok("OTP sent to " + email);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Taken");
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String enteredOtp = requestData.get("enteredOtp");

        if (email == null || enteredOtp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and enteredOtp are required.");
        }

        String verificationResult = otpGeneration.verifyOtp(email, enteredOtp, otpStorage);

        switch (verificationResult) {
            case "Success":
                return ResponseEntity.ok("OTP verified successfully!");

            case "Expired OTP":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("OTP has expired. Please request a new one.");

            case "Invalid OTP":
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid OTP. Please check and try again.");
        }
    }


}


