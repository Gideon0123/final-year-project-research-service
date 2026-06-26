package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface UserServiceClient {

    @GetMapping("/internal/users/{id}")
    UserProfileResponse getUserProfile(
            @PathVariable Long id
    );
}