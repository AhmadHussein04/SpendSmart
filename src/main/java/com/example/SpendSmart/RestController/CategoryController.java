package com.example.SpendSmart.RestController;


import com.example.SpendSmart.DTA.ExpenseResponse;
import com.example.SpendSmart.Entity.Category;
import com.example.SpendSmart.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("spendSmart/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse>  createCategory(@RequestBody Map<String, Object> payload) {

        String categoryName = (String) payload.get("categoryName");
        int userId = Integer.parseInt((String) payload.get("userId"));
        if(categoryName == null){
            return ResponseEntity.badRequest().build();
        }

        Category category = categoryService.createCategory(categoryName, userId);
            if(category!=null) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ExpenseResponse("Your Categorey "+categoryName+" has been added ", HttpStatus.OK.value(),category));
            }else {
                return ResponseEntity.status(409).build();
            }

    }
}
