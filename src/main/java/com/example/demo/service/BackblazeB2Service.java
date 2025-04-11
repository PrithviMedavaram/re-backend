package com.example.demo.service;

import com.backblaze.b2.client.B2Client;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.structures.B2UploadFileByNameRequest;
import com.backblaze.b2.client.structures.B2UploadFileResponse;
import com.example.demo.property.BackblazeB2Properties;
import com.example.demo.property.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class BackblazeB2Service {

    @Autowired
    private B2Client b2Client;

    @Autowired
    private BackblazeB2Properties backblazeB2Properties;

    public UploadFileResponse uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            B2UploadFileResponse response = b2Client.uploadFileByName(
                    B2UploadFileByNameRequest.builder(backblazeB2Properties.getBucketName(), fileName)
                            .setContentType(file.getContentType())
                            .setContentSource(B2ContentSource.ofInputStream(inputStream))
                            .build());

            return new UploadFileResponse(response.getFileId(), fileName, response.getUploadUrl());
        }
    }
}