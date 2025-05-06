package com.example.SpendSmart.RestController;

import com.example.SpendSmart.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spendSmart")
public class TokenRestController {


    @Autowired
    private TokenService jwtTokenService;

    @GetMapping("/validate")
    public String validateToken(@RequestHeader(name="Authorization") String token) {
        token = token.replace("Bearer ", "");
        boolean isValid = jwtTokenService.validateToken(token);
        if (isValid) {
            int userId = jwtTokenService.extractUserId(token);
            return "Valid Token for User ID: " + userId;
        } else {
            return "Invalid Token";
        }
    }

}
