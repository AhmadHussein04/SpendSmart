package com.example.SpendSmart.RestController;

import com.example.SpendSmart.DTA.APIResponse;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Service.UserLoginService;
import com.example.SpendSmart.Service.UserRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/spendSmart")
public class UserLoginController {
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterService.class);

        private UserLoginService userLoginService;


    @Autowired
        public UserLoginController(UserLoginService userLoginService) {
            this.userLoginService = userLoginService;
        }

        @PostMapping("/login")
        public ResponseEntity<APIResponse> login(@RequestBody Map<String, Object> payload) {

            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            return userLoginService.login(email,password);
        }
        @GetMapping("/recvie")
        public List<RegiUser> in(){
           return userLoginService.getall();
        }
    }





