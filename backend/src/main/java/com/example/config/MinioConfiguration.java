package com.example.config;

import io.minio.MinioClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfiguration {

    @Value("${spring.minio.endpoint}")
    private String endpoint;

    // 从配置文件读取MinIO用户名（accessKey）
    @Value("${spring.minio.username}")
    private String username;

    // 从配置文件读取MinIO密码（secretKey）
    @Value("${spring.minio.password}")
    private String password;

    @Bean
    public MinioClient minioClient(){
        log.info("Init MinIo client...");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username,password)
                .build();
    }
}
