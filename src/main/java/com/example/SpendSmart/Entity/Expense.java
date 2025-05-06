package com.example.SpendSmart.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expense")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private int expenseId;

    @Column(name = "expense_title")
    private String expenseTitle;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "currency_symbol")
    private String currencySymbol;

    @Column(name = "text")
    private String text;

    @Column(name = "expense_money")
    private double expensemoney;

    @Column(name = "category_name")
    private String categoryName;


    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Category category;

}