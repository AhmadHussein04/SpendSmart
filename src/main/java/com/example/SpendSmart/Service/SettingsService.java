package com.example.SpendSmart.Service;

import com.example.SpendSmart.DTA.APIResponse;
import com.example.SpendSmart.DTA.UpdatePersonalInfo;
import com.example.SpendSmart.Entity.Category;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository  settingsRepository;
    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);

    @Transactional
    public boolean deleteUserById(int userId,String password) {
        System.out.println("UserId: "+userId);
        Optional<RegiUser> user=settingsRepository.findById(userId);
        RegiUser  pass=user.get();
        if(!encoder.matches(password,pass.getPassword())){
            return false;
        }
        settingsRepository.deleteById(userId);
        return  true;
    }

    public boolean updateinfo(UpdatePersonalInfo updatePersonalInfo){
        int userid=updatePersonalInfo.getUserId();
        Optional <RegiUser> info = settingsRepository.findById(userid);
            if(info.isPresent()){
                System.out.println(info.get().getUserId());
                String name=updatePersonalInfo.getName();
                String email=updatePersonalInfo.getEmail();
                int phone=updatePersonalInfo.getPhoneNumber();
                info.get().setEmail(email);
                info.get().setName(name);
                info.get().setPhoneNumber(phone);
                settingsRepository.save(info.get());
                return true;
            }
        System.out.println(info.get().getUserId()+1);

        return  false;

    }

    public  boolean check(int userId){
        Optional <RegiUser> info = settingsRepository.findById(userId);
        if (info.get().getPin() == null) {
            return false;
        }
        return true;


    }

    public  boolean createnewpin(int userId,Integer pin){
        Optional <RegiUser> info = settingsRepository.findById(userId);
       boolean cat=check(userId);
       if(cat==false){
           info.get().setPin(pin);
           settingsRepository.save(info.get());
           return true;
       }
       return false;
    }
    public  int changepin(int userId,Integer oldpin,Integer newpin) {
        Optional<RegiUser> info = settingsRepository.findById(userId);
        boolean cat = check(userId);
        if (cat == true) {
            if (oldpin.equals(info.get().getPin())) {
                info.get().setPin(newpin);
                settingsRepository.save(info.get());
                return 1;
            }
            else{
                return -1;
            }
        }
        return 0;
    }

    public  boolean validatepin(int userId,Integer pin) {
        Optional<RegiUser> info = settingsRepository.findById(userId);
        boolean cat = check(userId);
        if (cat == true) {
            if (pin.equals(info.get().getPin())) {
                return true;
            }
            return false;
        }
        return false;

    }
}
