package com.example.RESEARCH_SERVICE.utils;

@ConfigurationProperties(
        prefix = "minio")
@Data
public class MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}