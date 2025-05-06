package com.example.SpendSmart.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static jakarta.persistence.CascadeType.*;


@Entity
@Table(name = "savings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Savings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saving_id")
    private int savingId;

    @Column(name = "expense_title")
    private String expenseTitle;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "currency_symbol")
    private String currencySymbol;

    @Column(name = "text")
    private String text;

    @Column(name = "saving_money")
    private double savingmoney;

    @Column(name = "goal_amount")
    private double goalamount;

    @Column(name = "category_name")
    private String categoryName;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Category category;
}
