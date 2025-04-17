package com.example.demo.model;

import java.util.Map;

public class UploadFileResponse {
    private String fileId;
    private String fileName;
    private Map info;

    // Constructors
    public UploadFileResponse(String fileId, String fileName, Map info) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.info = info;
    }

    // Getters and setters
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map getFileUrl() {
        return info;
    }

    public void setFileUrl(Map info) {
        this.info = info;
    }
}