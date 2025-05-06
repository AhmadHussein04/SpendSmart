package com.example.SpendSmart.Reposotory;

import com.example.SpendSmart.DTA.IncomeInfo;
import com.example.SpendSmart.Entity.Expense;
import com.example.SpendSmart.Entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Integer> {
    Iterable<Income> findAllByDate(LocalDate date);

    @Query("SELECT NEW com.example.SpendSmart.DTA.IncomeInfo(e.category,e.incomeTitle, e.text, e.date, e.incomemoney) FROM Income e WHERE e.user.userId = :userId AND e.date BETWEEN :startDate AND :endDate AND e.incomeTitle= 'salary'")
    List<IncomeInfo> findSavingMoneyByUserAndDate(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT  COALESCE(SUM(i.incomemoney), 0)  FROM Income i WHERE i.user.userId = :userId AND i.date BETWEEN :startDate AND :endDate")
    Double findTotalIncomeByUserAndDate(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i.incomeTitle,i.category,i.date,i.incomemoney FROM Income i WHERE i.user.id = :userId")
    List<Object[]> findByUserId(@Param("userId") int userId);

}
