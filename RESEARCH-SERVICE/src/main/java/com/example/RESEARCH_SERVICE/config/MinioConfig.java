package com.example.RESEARCH_SERVICE.config;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            MinioProperties properties
    ) {

        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(
                        properties.getAccessKey(),
                        properties.getSecretKey()
                )
                .build();
    }
}