package com.example.SpendSmart.DTA;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponse<T> {
        private String message;
        private int status;
        private T data;
}
