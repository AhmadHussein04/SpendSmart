package com.example.SpendSmart.RestController;


import com.example.SpendSmart.DTA.ExpenseResponse;
import com.example.SpendSmart.Service.CategoryService;
import com.example.SpendSmart.Service.TotalBalenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("spendSmart/balance")
public class TotalBalenceController {


    @Autowired
    private TotalBalenceService totalBalenceService;

    @GetMapping("/Monthly/{userId}")
    public double getMonthlyBalnce(@PathVariable int userId) {
    return totalBalenceService.totalbalencemonthly(userId);
    }
    @GetMapping("/weekly/{userId}")
    public double getWeeklyBalnce(@PathVariable int userId) {
        return totalBalenceService.totalbalenceweekly(userId);
    }

}
