package com.example.SpendSmart.RestController;

import com.example.SpendSmart.DTA.Exoensefo;
import com.example.SpendSmart.DTA.ExpenseForMonthlyCategory;
import com.example.SpendSmart.DTA.ExpenseResponse;
import com.example.SpendSmart.DTA.MonthlyExpenseDTO;
import com.example.SpendSmart.Entity.Expense;
import com.example.SpendSmart.Service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/spendSmart/expenses")
public class ExpenseController {


    @Autowired
    private ExpenseService expenseService;



    @GetMapping("/last7Days/{userId}")
    public ResponseEntity<ExpenseResponse> getTotalExpensesForLast7DaysByUser(@PathVariable int userId, @RequestParam String startDate) {  // Added startDate parameter

        List<Map<String, Object>> expenses = expenseService.getTotalExpensesForLast7days(userId, startDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(), expenses));
    }

    @GetMapping("/last4Weeks/{userId}")
    public ResponseEntity<ExpenseResponse> getTotalExpensesForLast4WeeksByUser(@PathVariable int userId, @RequestParam String startDate) {  // Start date as a query parameter
        LocalDate parsedStartDate = LocalDate.parse(startDate);

        List<Double> expenses = expenseService.getTotalExpensesForLast4Weeks(userId, parsedStartDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(), expenses));
    }

    @GetMapping("/last6Months/{userId}")
    public ResponseEntity<ExpenseResponse> getLast6MonthsSpending(@PathVariable int userId, @RequestParam String startDate) {  // Start date as a query parameter

        // Convert startDate string to LocalDate
        LocalDate parsedStartDate = LocalDate.parse(startDate);

        // Fetch total expenses for the last 6 months based on the provided startDate
        List<Map<String, Object>> expenses = expenseService.getTotalExpensesForLast6Months(userId, parsedStartDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(), expenses));
    }

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> addOrUpdateExpense(@RequestBody Map<String, Object> payload) {
        Expense expense = expenseService.addExpense(payload);
        if(expense!=null){
            Double expensemoney = Double.parseDouble((String) payload.get("expenseMoney"));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ExpenseResponse("Your Expense has been added "+expensemoney, HttpStatus.OK.value(),expense));

        }else{
            return ResponseEntity.status(409).build();

        }
    }

    @GetMapping("/Monthly/{userId}")
    public ResponseEntity<ExpenseResponse> getMonthlySpending(@PathVariable int userId) {
        Double expe= expenseService.getCurrentMonthExpenses(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(),expe));
    }


    @GetMapping("/monthlyinfo/{userId}")
    public ResponseEntity<ExpenseResponse> getMonthlyInfo(@PathVariable int userId) {

        List<Exoensefo> expe= expenseService.getExpenseOfMonth(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ExpenseResponse("Your data", HttpStatus.OK.value(),expe));
    }

    @GetMapping("/weeklyfood/{userId}")
    public double getWeeklyBalncefoood(@PathVariable int userId) {
        return expenseService.getWeeklyFoodExpenses(userId);
    }

    @GetMapping("/category/{categoryName}/user/{userId}/monthly")
    public CompletableFuture<List<MonthlyExpenseDTO>>getExpensesByCategoryAndUser(@PathVariable String categoryName, @PathVariable int userId) {
        return expenseService.getExpensesByCategoryAndUser(categoryName, userId);
    }
    @GetMapping("/categorysum/{userId}")
    public List<ExpenseForMonthlyCategory> getCategorySumsByMonthAndYearForUser(@PathVariable int userId) {
        return expenseService.getCategorySumsByMonthAndYearForUser(userId);
    }
    @GetMapping("/filtered/{userId}")
    public List<MonthlyExpenseDTO> getExpensesByUser(@PathVariable int userId) {
        // Fetching the expenses data from the service
        return expenseService.getExpensesByUser(userId);

    }
}