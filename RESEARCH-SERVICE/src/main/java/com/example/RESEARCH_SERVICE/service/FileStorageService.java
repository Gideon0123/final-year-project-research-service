package com.example.RESEARCH_SERVICE.service;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String objectKey);
    InputStream downloadFile(String objectKey);
    void deleteFile(String objectKey);
    boolean exists(String objectKey);
}