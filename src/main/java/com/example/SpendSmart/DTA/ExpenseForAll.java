package com.example.SpendSmart.DTA;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseForAll {
    private String expenseTitle;
    private String categoryName;
    private String date;
    private double expenseMoney;
    private String text;

}
