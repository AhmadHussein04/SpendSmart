package com.example.SpendSmart.Reposotory;


import com.example.SpendSmart.Entity.RegiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<RegiUser, Integer> {
    void deleteById(int userId);

    Optional<RegiUser> findById(int userId);


}
