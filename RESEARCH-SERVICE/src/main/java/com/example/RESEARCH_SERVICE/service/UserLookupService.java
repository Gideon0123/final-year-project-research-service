package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.UserSummaryResponse;

public interface UserLookupService {

    UserSummaryResponse getUserProfile(
            Long userId
    );

}