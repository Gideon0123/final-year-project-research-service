package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.mapper.ResearchPaperMapper;
import com.example.RESEARCH_SERVICE.publisher.ResearchEventPublisher;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import com.example.RESEARCH_SERVICE.repository.ResearchPaperRepository;
import com.example.RESEARCH_SERVICE.utils.ResearchAuditLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ResearchPaperService {

    private final ResearchPaperRepository paperRepository;
    private final ResearchCategoryRepository categoryRepository;
    private final ResearchPaperMapper mapper;
    private final CurrentUserService currentUserService;
    private final ResearchAuditLogger auditLogger;
    private final ResearchEventPublisher eventPublisher;
}