package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.ApiResponse;
import com.example.RESEARCH_SERVICE.dto.UserSummaryResponse;
import com.example.RESEARCH_SERVICE.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLookupServiceImpl
        implements UserLookupService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserSummaryResponse getUserProfile(
            Long userId
    ) {
        ApiResponse<UserSummaryResponse> response = userServiceClient.getUserProfile(userId);

        if (response == null || !response.isSuccess() || response.getData() == null) {

            throw new ResourceNotFoundException(
                    "User profile not found."
            );
        }

        return response.getData();
    }

}