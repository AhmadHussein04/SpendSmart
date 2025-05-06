package com.example.SpendSmart.RestController;


import com.example.SpendSmart.DTA.APIResponse;
import com.example.SpendSmart.Service.UserForgetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/spendSmart")
public class UserForgetPasswordController {
    private UserForgetPasswordService userForgetPasswordService;

    @Autowired
    public UserForgetPasswordController(UserForgetPasswordService userForgetPasswordService) {
        this.userForgetPasswordService = userForgetPasswordService;
    }


    @PostMapping("/checkemail")
    public ResponseEntity<APIResponse> checkemail(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        int num= userForgetPasswordService.checkemail(email);

        if(num!=-1){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new APIResponse("Email found", HttpStatus.OK.value(),num));
        }else{
            return  ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse("No Email Found", HttpStatus.NOT_FOUND.value(), -1));
        }
    }
    @PatchMapping("/forgetpassword")
    public ResponseEntity<APIResponse> forget(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String password = (String) payload.get("password");
        int num= userForgetPasswordService.forgetpass(email,password);

        if(num>=0){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new APIResponse("Password Changed Successful!", HttpStatus.OK.value(),num));
        }else{
            return  ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse("No Email Found", HttpStatus.NOT_FOUND.value(), -1));
        }
    }
}
