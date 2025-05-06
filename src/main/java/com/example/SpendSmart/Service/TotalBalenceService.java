package com.example.SpendSmart.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TotalBalenceService {
    @Autowired
    private ExpenseService expenseServicee;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private SavingsService  savingsService;

    public double totalbalencemonthly(int userId){
        double exp=expenseServicee.getCurrentMonthExpenses(userId);
        double incom=incomeService.getCurrentMonthIncome(userId);
        double savin=savingsService.getCurrentMonthSavings(userId);
        double total= incom-(exp+savin);
        return total;
    }

    public double totalbalenceweekly(int userId){
        double exp=expenseServicee.getWeeklyExpense(userId);
        double incom=incomeService.getWeeklyIncome(userId);
        double savin=savingsService.getWeeklySvings(userId);
        double total= incom-(exp+savin);
        return total;
    }
}
