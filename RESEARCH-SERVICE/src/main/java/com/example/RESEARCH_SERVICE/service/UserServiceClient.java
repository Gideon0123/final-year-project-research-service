package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.config.FeignConfig;
import com.example.RESEARCH_SERVICE.dto.ApiResponse;
import com.example.RESEARCH_SERVICE.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "AUTH-SERVICE",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/users/internal/{id}")
    ApiResponse<UserSummaryResponse> getUserProfile(
            @PathVariable Long id
    );

}