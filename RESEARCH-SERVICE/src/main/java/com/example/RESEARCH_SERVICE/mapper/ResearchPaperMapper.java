package com.example.RESEARCH_SERVICE.mapper;

import com.example.RESEARCH_SERVICE.dto.ResearchPaperResponse;
import com.example.RESEARCH_SERVICE.dto.ResearchPaperSummaryResponse;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResearchPaperMapper {

    @Mapping(
            target = "category",
            source = "category.name"
    )
    ResearchPaperResponse toResponse(ResearchPaper paper);

    @Mapping(
            target = "category",
            source = "category.name"
    )
    ResearchPaperSummaryResponse toSummary(ResearchPaper paper);
}