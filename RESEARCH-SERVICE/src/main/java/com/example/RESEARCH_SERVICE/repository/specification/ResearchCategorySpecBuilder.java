package com.example.RESEARCH_SERVICE.repository.specification;

import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResearchCategorySpecBuilder {

    public static Specification<ResearchCategory> build(

            String keyword,
            Long id,
            String name
    ) {
        return Specification.where(ResearchCategorySpec.keyword(keyword))
                .and(ResearchCategorySpec.hasId(id))
                .and(ResearchCategorySpec.name(name));
    }
}
