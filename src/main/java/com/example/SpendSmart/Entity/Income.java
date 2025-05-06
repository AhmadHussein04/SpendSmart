package com.example.SpendSmart.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_id")
    private int incomeId;

    @Column(name = "income_title")
    private String incomeTitle;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "currency_symbol")
    private String currencySymbol;

    @Column(name = "category")
    private String category;


    @Column(name = "text")
    private String text;

    @Column(name = "income_money")
    private double incomemoney;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private RegiUser user;

}
