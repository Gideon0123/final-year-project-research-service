package com.example.RESEARCH_SERVICE.service;

import com.example.RESEARCH_SERVICE.dto.ResearchCandidateResponse;
import com.example.RESEARCH_SERVICE.dto.ResearchFileResponse;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import com.example.RESEARCH_SERVICE.exception.FileStorageException;
import com.example.RESEARCH_SERVICE.exception.ResourceNotFoundException;
import com.example.RESEARCH_SERVICE.mapper.ResearchPaperMapper;
import com.example.RESEARCH_SERVICE.repository.ResearchPaperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResearchFileServiceImpl implements ResearchFileService {

    private final ResearchPaperRepository paperRepository;
    private final FileStorageService fileStorageService;
    private final ResearchPaperMapper mapper;

    private ResearchPaper getPaperEntity(
            Long paperId
    ) {
        return paperRepository.findById(paperId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Research paper not found with id: "
                                + paperId
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResearchFileResponse getResearchFileMetadata(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);
        validateStorageKey(paper);

        if (!fileStorageService.exists(paper.getStorageKey())) {
            throw new FileStorageException(
                    "Research file does not exist in storage."
            );
        }

        return ResearchFileResponse.builder()
                .paperId(paper.getId())
                .fileName(paper.getFileName())
                .contentType(paper.getContentType())
                .fileSize(paper.getFileSize())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public InputStream downloadResearchFile(
            Long paperId
    ) {
        ResearchPaper paper = getPaperEntity(paperId);
        validateStorageKey(paper);

        if (!fileStorageService.exists(paper.getStorageKey())) {
            throw new FileStorageException(
                    "Research file does not exist in storage."
            );
        }

        log.info(
                "Downloading research file for paperId={}, storageKey={}",
                paperId,
                paper.getStorageKey()
        );

        return fileStorageService.downloadFile(
                paper.getStorageKey()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResearchCandidateResponse> getPublishedCandidates() {

        return paperRepository.findByStatusAndVisibility(
                        ResearchStatus.PUBLISHED,
                        ResearchVisibility.PUBLIC,
                        Pageable.unpaged()
                )
                .stream()
                .map(mapper::toCandidateResponse)
                .toList();
    }


    private void validateStorageKey(
            ResearchPaper paper
    ) {
        if (paper.getStorageKey() == null || paper.getStorageKey().isBlank()) {
            throw new ResourceNotFoundException(
                    "Research paper has no associated file"
            );
        }
    }
}