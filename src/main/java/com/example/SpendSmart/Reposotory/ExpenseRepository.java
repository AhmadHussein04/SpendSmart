package com.example.SpendSmart.Reposotory;


import com.example.SpendSmart.DTA.Exoensefo;
import com.example.SpendSmart.Entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    Iterable<Expense> findAllByDate(LocalDate date);


    @Query("SELECT e.expenseTitle, e.category.categoryName ,e.date, e.expensemoney FROM Expense e WHERE e.category.user.id = :userId")
    List<Object[]> findByCategory_User_Id(@Param("userId") int userId);

   @Query("SELECT c.expenseTitle, c.category.categoryName, c.date, c.expensemoney,c.text FROM Expense c WHERE c.category.categoryName = :categoryName AND c.category.user.userId = :userId")
   List<Object[]> findExpensesGroupedByMonth(@Param("categoryName") String categoryName, @Param("userId") int userId);

    @Query("SELECT COALESCE(SUM(e.expensemoney), 0) FROM Expense e WHERE e.category.user.userId = :userId AND e.category.categoryName = 'food' AND e.date BETWEEN :startDate AND :endDate")
    Double findWeeklyFoodExpenses(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT NEW com.example.SpendSmart.DTA.Exoensefo(e.expenseTitle, e.categoryName, e.date, e.expensemoney) FROM Expense e WHERE e.category.user.userId = :userId AND e.date BETWEEN :startDate AND :endDate")
    List<Exoensefo> findExpenseMoneyByUserAndDate(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.expensemoney), 0) FROM Expense e WHERE e.category.user.userId = :userId AND e.date BETWEEN :startDate AND :endDate")
    Double findTotalExpenseMoneyByUserAndDate(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.expenseTitle, c.category.categoryName, c.date, c.expensemoney,c.text FROM Expense c WHERE c.category.user.id = :userId AND c.category.categoryName NOT IN ('food', 'gifts', 'rent', 'transport', 'groceries', 'entertainment')")
    List<Object[]> findExpensesByCategoryAndUserId(@Param("userId") int userId);
}

