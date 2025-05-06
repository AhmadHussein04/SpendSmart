package com.example.SpendSmart.RestController;

import com.example.SpendSmart.DTA.GeneralResponse;
import com.example.SpendSmart.Entity.RegiUser;
import com.example.SpendSmart.Reposotory.UserRegisterRepostray;
import com.example.SpendSmart.Service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    public UserRegisterRepostray userRegisterRepostray;

    // Upload a file to S3
    @PostMapping("/upload/{userId}")
    public ResponseEntity<GeneralResponse> uploadFile(@PathVariable("userId")int userId, @RequestParam("file") MultipartFile file) throws IOException {

        // Convert MultipartFile to File
        String path=s3Service.uploadFile(file);
        Optional<RegiUser> info=userRegisterRepostray.findById(userId);
    if(info!=null){
    info.get().setImage(path);
    userRegisterRepostray.save(info.get());
    }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GeneralResponse(path, "200"));
    }

    // Download a file from S3
    @GetMapping("/download/{fileName}")
    public String downloadFile(@PathVariable String fileName) {
        return s3Service.downloadFile(fileName).getKey();
    }
}