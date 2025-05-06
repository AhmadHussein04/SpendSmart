package com.example.SpendSmart.RestController;


import com.example.SpendSmart.DTA.UpdatePersonalInfo;
import com.example.SpendSmart.Service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/spendSmart/settings")
public class SettingController {

    @Autowired
    private SettingsService settingsService;

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Object> payload) {
        int userId = Integer.parseInt(payload.get("userId").toString());

        String password = (String) payload.get("password");
        boolean num = settingsService.deleteUserById(userId, password);

        if (num == false) {
            return ResponseEntity.status(401).build();

        }
        return ResponseEntity.ok().build();

    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateinfo(@RequestBody UpdatePersonalInfo updatePersonalInfo) {

        boolean num = settingsService.updateinfo(updatePersonalInfo);

        if (num==false) {
            return ResponseEntity.status(401).build();

        }
        return ResponseEntity.ok().build();

    }

    @GetMapping("/checkpinexist/{userId}")
    public ResponseEntity<?> checkpinexist(@PathVariable int userId) {
        boolean num = settingsService.check(userId);
        if (num==false) {
            return ResponseEntity.status(200).build();
        }
        return ResponseEntity.status(401).build();
    }
    @PostMapping("/createpin")
    public ResponseEntity<?> checkpin(@RequestBody Map<String, Object> payload) {
        Integer pin = Integer.parseInt(payload.get("pin").toString());
        int userId = Integer.parseInt(payload.get("userId").toString());
        boolean num = settingsService.createnewpin(userId,pin);
        if (num==true) {
            return ResponseEntity.status(200).build();
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/changepin")
    public ResponseEntity<?> changepin(@RequestBody Map<String, Object> payload) {
        Integer oldpin = Integer.parseInt(payload.get("oldpin").toString());
        Integer newpin = Integer.parseInt(payload.get("newpin").toString());
        int userId = Integer.parseInt(payload.get("userId").toString());
        int num = settingsService.changepin(userId,oldpin,newpin);
        if (num==1) {
            return ResponseEntity.status(200).build();
        } else if (num==-1) {
            return ResponseEntity.status(402).build();
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatepin(@RequestBody Map<String, Object> payload) {
        Integer pin = Integer.parseInt(payload.get("oldpin").toString());
        int userId = Integer.parseInt(payload.get("userId").toString());
        boolean num = settingsService.validatepin(userId,pin);
        if (num==true) {
            return ResponseEntity.status(200).build();
        }
        return ResponseEntity.status(401).build();
    }




}
