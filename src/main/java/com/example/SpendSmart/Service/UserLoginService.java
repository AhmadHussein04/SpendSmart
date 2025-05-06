package com.example.SpendSmart.Service;


import com.example.SpendSmart.DTA.APIResponse;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.UserLoginRepostory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserLoginService {

    private UserLoginRepostory userLoginService;

    @Autowired
    public UserLoginService(UserLoginRepostory userLoginService) {
        this.userLoginService = userLoginService;
    }

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);


    public ResponseEntity<APIResponse> login(String email,String password) {

        String normalizedTitle = email.trim().toLowerCase();

        Optional <RegiUser> info=userLoginService.findByEmail(normalizedTitle);

          if(info.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse("No Email Found", HttpStatus.NOT_FOUND.value(),-1));
          }
        RegiUser  pass=info.get();
          if(!encoder.matches(password,pass.getPassword())){
              return ResponseEntity
                      .status(HttpStatus.UNAUTHORIZED)
                      .body(new APIResponse("Incorrect Password", HttpStatus.UNAUTHORIZED.value(),info.get().getUserId()));
          }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new APIResponse("Login Successful!", HttpStatus.OK.value(),info.get().getUserId()));
    }
    public List<RegiUser> getall(){
        return userLoginService.findAll();
    }
}
