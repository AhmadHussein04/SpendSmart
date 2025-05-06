package com.example.SpendSmart.Reposotory;

import com.example.SpendSmart.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Integer> {

   @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName AND c.user.userId = :userId")
    Optional<Category> findByCategoryNameAndUserId(@Param("categoryName") String name, @Param("userId") int userId);

    boolean existsByCategoryNameAndUserUserId(String categoryName, int userId);

}
