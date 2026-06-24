package com.example.RESEARCH_SERVICE.repository;

import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ResearchCategoryRepository extends JpaRepository<ResearchCategory, Long>,
        JpaSpecificationExecutor<ResearchCategory> {

    boolean existsByNameIgnoreCase(String name);
}