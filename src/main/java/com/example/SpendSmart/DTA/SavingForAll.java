package com.example.SpendSmart.DTA;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SavingForAll {
    private String expenseTitle;
    private String categoryName;
    private String date;
    private double savingMoney;
    private double goalMoney;
}
