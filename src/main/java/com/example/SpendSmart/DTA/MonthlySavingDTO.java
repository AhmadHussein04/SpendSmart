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
public class MonthlySavingDTO {
    private String monthName;
    private List<SavingForAll> saving;
    private double totalMoney;

}
