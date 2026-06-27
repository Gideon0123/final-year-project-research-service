package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.exception.FileStorageException;
import com.example.RESEARCH_SERVICE.utils.MinioProperties;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String uploadFile(
            MultipartFile file,
            String objectKey
    ) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1L
                            )
                            .contentType(file.getContentType())
                            .build()
            );
            log.info(
                    "File uploaded successfully: {}",
                    objectKey
            );
            return objectKey;
        }

        catch (Exception ex) {
            throw new FileStorageException("Failed to upload file", ex);
        }
    }

    @Override
    public InputStream downloadFile(
            String objectKey
    ) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );

        }

        catch (Exception ex) {
            throw new FileStorageException("Failed to download file", ex);
        }
    }

    @Override
    public void deleteFile(
            String objectKey
    ) {
        try {
            minioClient.removeObject(

                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        }

        catch (Exception ex) {
            throw new FileStorageException("Failed to delete file", ex);
        }
    }

    @Override
    public boolean exists(
            String objectKey
    ) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
            return true;
        }
        catch (Exception ex) {

            return false;
        }
    }
}