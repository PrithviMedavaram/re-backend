package com.example.demo.property;

public class UploadFileResponse {
    private String fileId;
    private String fileName;
    private String fileUrl;

    // Constructors
    public UploadFileResponse(String fileId, String fileName, String fileUrl) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}