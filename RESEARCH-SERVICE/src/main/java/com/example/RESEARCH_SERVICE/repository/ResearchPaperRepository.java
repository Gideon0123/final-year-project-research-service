package com.example.RESEARCH_SERVICE.repository;

import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long>,
        JpaSpecificationExecutor<ResearchPaper> {

    Page<ResearchPaper> findByStatus(ResearchStatus status, Pageable pageable);

    Page<ResearchPaper> findByCategoryId(Long categoryId, Pageable pageable);

    boolean existsByTitleIgnoreCase(String title);

    Optional<ResearchPaper> findByIdAndAuthorId(Long id, Long authorId);

    Page<ResearchPaper> findByAuthorId(Long authorId, Pageable pageable);
}