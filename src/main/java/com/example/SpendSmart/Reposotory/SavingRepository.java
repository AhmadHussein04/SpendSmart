package com.example.SpendSmart.Reposotory;

import com.example.SpendSmart.Entity.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SavingRepository extends JpaRepository<Savings, Integer> {

    Iterable<Savings> findAllByDate(LocalDate date);


    @Query("SELECT SUM(c.savingmoney) AS totalSavings FROM Savings c WHERE c.category.categoryName = :categoryName AND c.category.user.userId = :userId")
     Double findtotalsavings(@Param("categoryName") String categoryName, @Param("userId") int userId);


    @Query("SELECT c.expenseTitle, c.category.categoryName, c.date, c.savingmoney ,c.goalamount FROM Savings c WHERE c.category.categoryName = :categoryName AND c.category.user.userId = :userId")
    List<Object[]> findSavingGroupedByMonth(@Param("categoryName") String categoryName, @Param("userId") int userId);

    @Query("SELECT COALESCE(SUM(s.savingmoney), 0)  FROM Savings s WHERE s.category.user.userId = :userId AND s.date BETWEEN :startDate AND :endDate")
    Double findTotalSavingsByUserAndDate(@Param("userId") int userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
