package com.example.SpendSmart.Service;


import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.UserForgetPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserForgetPasswordService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterService.class);
    private UserForgetPassword userforgetpassword;

    @Autowired
    public UserForgetPasswordService(UserForgetPassword userforgetpassword) {
        this.userforgetpassword = userforgetpassword;
    }

    public int checkemail(String email){
        String normalizedTitle = email.trim().toLowerCase();
        Optional <RegiUser> info = userforgetpassword.findByEmail(normalizedTitle);
        if(info.isPresent()){
            return  info.get().getUserId();
        }
        return  -1;
    }
    public int forgetpass(String email,String password) {
        String normalizedTitle = email.trim().toLowerCase();
        Optional <RegiUser> info = userforgetpassword.findByEmail(normalizedTitle);
        if (info.isPresent()) {

            logger.info("pass1={}",
                    info.get().getPassword());
            info.get().setPassword(password);
            logger.info("pass2={}",
                    info.get().getPassword());
            logger.info("pass3={}",
                    password);

            userforgetpassword.save(info.get());

            return info.get().getUserId();
        }
        return -1;
    }
}