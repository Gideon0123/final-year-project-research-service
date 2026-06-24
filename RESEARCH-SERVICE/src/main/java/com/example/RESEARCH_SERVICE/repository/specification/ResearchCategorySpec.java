package com.example.RESEARCH_SERVICE.repository.specification;

import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import org.springframework.data.jpa.domain.Specification;

public class ResearchCategorySpec {

    public static Specification<ResearchCategory> keyword(
            String keyword
    ) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String value = "%" + keyword + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.get("name")),
                            value.toLowerCase()
                    )
            );
        };
    }

    public static Specification<ResearchCategory> hasId(
            Long id
    ) {
        return (root, query, cb) ->

                id == null
                        ? cb.conjunction()
                        : cb.equal(root.get("id"), id);
    }

    public static Specification<ResearchCategory> name(
            String name
    ) {
        return (root, query, cb) ->

                name == null || name.isBlank()
                        ? cb.conjunction()
                        : cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}
