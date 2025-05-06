package com.example.SpendSmart.RestController;

import com.example.SpendSmart.DTA.ExpenseResponse;
import com.example.SpendSmart.DTA.IncomeInfo;
import com.example.SpendSmart.DTA.MonthlyExpenseDTO;
import com.example.SpendSmart.DTA.MonthlySavingDTO;
import com.example.SpendSmart.Entity.Savings;
import com.example.SpendSmart.Service.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/spendSmart/savings")
public class SavingController {
    private SavingsService savingsService;

    @Autowired
    public SavingController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    @PostMapping("/savedmoney")
    public ResponseEntity<ExpenseResponse> saveSaving(@RequestBody Map<String, Object> payload) {
        Savings saving = savingsService.saveSaving(payload);
        if (saving != null) {
            Double savingmoney = Double.parseDouble((String) payload.get("savingmoney"));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ExpenseResponse("Your Money  has been added " + savingmoney, HttpStatus.OK.value(), saving));

        } else {
            return ResponseEntity.status(409).build();

        }
    }

    @GetMapping("/category/{categoryName}/user/{userId}/monthly")
    public CompletableFuture<List<MonthlySavingDTO>> getExpensesByCategoryAndUser(@PathVariable String categoryName, @PathVariable int userId) {
        return savingsService.getSavingByCategoryAndUser(categoryName, userId);
    }
}
