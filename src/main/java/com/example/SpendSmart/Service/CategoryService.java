package com.example.SpendSmart.Service;

import com.example.SpendSmart.Entity.Category;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.CategoryRepository;
import com.example.SpendSmart.Reposotory.UserRegisterRepostray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRegisterRepostray userRepository;

    public Category createCategory(String categoryName, int userId) {
        Optional<RegiUser> user = userRepository.findById(userId);
        boolean categoryExists = categoryRepository.existsByCategoryNameAndUserUserId(categoryName, userId);
        if (categoryExists) {
            return  null;
        } else {
            RegiUser users = user.get();
            Category category = new Category();
            String normalizedTitln = categoryName.toLowerCase();
            category.setCategoryName(normalizedTitln);
            category.setUser(users);
            categoryRepository.save(category);
            return category;
        }
    }
}
