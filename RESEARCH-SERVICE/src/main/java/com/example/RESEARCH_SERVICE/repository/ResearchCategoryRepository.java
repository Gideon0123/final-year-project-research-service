package com.example.RESEARCH_SERVICE.repository;

import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchCategoryRepository extends JpaRepository<ResearchCategory, Long> {

    boolean existsByNameIgnoreCase(String name);
}