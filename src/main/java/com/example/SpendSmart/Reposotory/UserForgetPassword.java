package com.example.SpendSmart.Reposotory;


import com.example.SpendSmart.Entity.RegiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserForgetPassword extends JpaRepository<RegiUser,Integer> {
    Optional<RegiUser> findByEmail(String email);

}