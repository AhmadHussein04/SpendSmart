package com.example.SpendSmart.Service;

import com.example.SpendSmart.Entity.Category;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.CategoryRepository;
import com.example.SpendSmart.Reposotory.OTPGeneration;
import com.example.SpendSmart.Reposotory.UserRegisterRepostray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.*;

@Service
public class UserRegisterService {
    @Autowired
    private UserRegisterRepostray userRegisterService;
    @Autowired
    private CategoryRepository categoryService;


    private static final Logger logger = LoggerFactory.getLogger(UserRegisterService.class);

    private BCryptPasswordEncoder encoder =new BCryptPasswordEncoder(12);



    private Map < String, String > otpStorage = new HashMap < > ();

    public int Register(RegiUser request) {
        String normalizedTitle = request.getEmail().trim().toLowerCase();
        Optional < RegiUser > info = userRegisterService.findByEmail(normalizedTitle);

        logger.info("📩 Received Registration Request: Name={}, Email={}, PhoneNumber={}",
                request.getName(), request.getEmail(), request.getPhoneNumber());

        RegiUser newuser = new RegiUser();
        if (info.isEmpty()) {
            newuser.setName(request.getName());
            newuser.setEmail(normalizedTitle);
            newuser.setPassword(encoder.encode(request.getPassword()));
            newuser.setDateofbirth(request.getDateofbirth());
            newuser.setPhoneNumber(request.getPhoneNumber());
            userRegisterService.save(newuser);

            List<String> defaultCategories = List.of("food", "transport", "entertainment","groceries","rent","gifts");
            List<Category> categories = new ArrayList<>();

            for (String cat : defaultCategories) {
                Category category = new Category();
                category.setCategoryName(cat);
                category.setUser(newuser);
                categories.add(category);
            }
            categoryService.saveAll(categories);

            return newuser.getUserId();
            }
            logger.info("✅ Saving New User: Name={}, Email={}, PhoneNumber={}",
                    newuser.getName(), newuser.getEmail(), newuser.getPhoneNumber());

        return -1;
    }

    public Optional<RegiUser> getUserInfoById(int id) {
        return userRegisterService.findById(id);
    }
}