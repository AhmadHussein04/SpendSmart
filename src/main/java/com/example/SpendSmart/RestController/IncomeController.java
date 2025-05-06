package com.example.SpendSmart.RestController;

import com.example.SpendSmart.DTA.ExpenseForMonthlyCategory;
import com.example.SpendSmart.DTA.ExpenseResponse;
import com.example.SpendSmart.DTA.IncomeInfo;
import com.example.SpendSmart.Entity.Income;
import com.example.SpendSmart.Service.CategoryService;
import com.example.SpendSmart.Service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("spendSmart/salary")
public class IncomeController {


    @Autowired
    private IncomeService incomeService;

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> createCategory(@RequestBody Map<String, Object> payload) {
    Income num =incomeService.createIncome(payload);

    if(num!=null){
        Double incomemoney = Double.parseDouble((String) payload.get("incomemoney"));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your Salary has been added "+incomemoney, HttpStatus.OK.value(),num));

    }else{
        return ResponseEntity.status(409).build();

    }
    }

    @GetMapping("/last7Days/{userId}")
    public ResponseEntity<ExpenseResponse>  getTotalIncomeForLast7DaysByUser(@PathVariable int userId) {
        List<Map<String, Object>> incomeData = incomeService.getIncomeForLast7days(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(), incomeData));
    }

    @GetMapping("/last4Weeks/{userId}")
    public ResponseEntity<ExpenseResponse> getLast4WeeksIncome(@PathVariable int userId) {

        List<Double> incom= incomeService.getTotalIncomeForLast4Weeks(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(),incom));
    }

    @GetMapping("/last6Months/{userId}")
    public ResponseEntity<ExpenseResponse> getLast6MonthsIncome(@PathVariable int userId) {
        List<Double> incom= incomeService.getTotalIncomeForLast6Months(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(),incom));
    }

    @GetMapping("/Monthly/{userId}")
    public ResponseEntity<ExpenseResponse> getMonthlyIncome(@PathVariable int userId) {
    Double incom= incomeService.getCurrentMonthIncome(userId);

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ExpenseResponse("Your data", HttpStatus.OK.value(),incom));
}


    @GetMapping("/monthlyinfo/{userId}")
    public ResponseEntity<ExpenseResponse> getMonthlyInfo(@PathVariable int userId) {

        List<IncomeInfo> expe= incomeService.getSavingOfMonth(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(),expe));
    }


    @GetMapping("/categorysum/{userId}")
    public List<ExpenseForMonthlyCategory> getCategorySumsByMonthAndYearForUser(@PathVariable int userId) {
        return incomeService.getIncomeCategorySumsByMonthAndYearForUser(userId);
    }
    }
