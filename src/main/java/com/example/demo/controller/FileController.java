package com.example.demo.controller;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.example.demo.model.UploadFileResponse;
import com.example.demo.service.BackblazeB2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileController {

    @Autowired
    private BackblazeB2Service backblazeB2Service;

    @PostMapping("/api/upload")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            UploadFileResponse response = backblazeB2Service.uploadFile(file);
            return ResponseEntity.ok(response);
        } catch (IOException | B2Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
