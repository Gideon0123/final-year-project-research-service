package com.example.RESEARCH_SERVICE.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditLogService {

    public void logPaperCreated(
            Long paperId,
            Long userId,
            String title
    ) {
        log.info(
                "Research Paper Created | PaperId={} | UserId={} | Title={}",
                paperId,
                userId,
                title
        );
    }
}