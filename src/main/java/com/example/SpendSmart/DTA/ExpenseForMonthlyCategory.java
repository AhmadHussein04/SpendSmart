package com.example.SpendSmart.DTA;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseForMonthlyCategory {
    private String month;
    private List<ExpenseForCategory> expenseDetails;
}
