package com.example.RESEARCH_SERVICE.repository.specification;

import com.example.RESEARCH_SERVICE.dto.ResearchPaperSearchRequest;
import com.example.RESEARCH_SERVICE.entity.ResearchPaper;
import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResearchPaperSpecification {

    public static Specification<ResearchPaper> searchKeyword(
            String keyword
    ) {
        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.get("title")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(root.get("abstractText")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(root.get("keywords")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(root.get("status")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(root.get("visibility")),
                            pattern
                    )
            );
        };

    }

    public static Specification<ResearchPaper> hasCategory(
            Long categoryId
    ) {
        return (root, query, cb) -> {

            if (categoryId == null) {
                return null;
            }

            return cb.equal(
                    root.get("category").get("id"),
                    categoryId
            );
        };
    }

    public static Specification<ResearchPaper> hasStatus(
            ResearchStatus status
    ) {
        return (root, query, cb) -> {

            if (status == null) {
                return null;
            }

            return cb.equal(
                    root.get("status"),
                    status
            );
        };
    }

    public static Specification<ResearchPaper> hasVisibility(
            ResearchVisibility visibility
    ) {
        return (root, query, cb) -> {

            if (visibility == null) {
                return null;
            }

            return cb.equal(
                    root.get("visibility"),
                    visibility
            );
        };
    }

    public static Specification<ResearchPaper> hasAuthor(
            Long authorId
    ) {

        return (root, query, cb) -> {

            if (authorId == null) {
                return null;
            }

            return cb.equal(
                    root.get("authorId"),
                    authorId
            );
        };
    }

    public static Specification<ResearchPaper> hasReviewer(
            Long reviewerId
    ) {
        return (root, query, cb) -> {

            if (reviewerId == null) {
                return null;
            }

            return cb.equal(
                    root.get("reviewerId"),
                    reviewerId
            );
        };
    }

    public static Specification<ResearchPaper> createdAfter(
            LocalDateTime createdAfter
    ) {
        return (root, query, cb) ->

                createdAfter == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        createdAfter
                );
    }

    public static Specification<ResearchPaper> createdBefore(
            LocalDateTime createdBefore
    ) {
        return (root, query, cb) ->

                createdBefore == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        createdBefore
                );
    }

    public static Specification<ResearchPaper> build(
            ResearchPaperSearchRequest request
    ) {

        return Specification.where(
                searchKeyword(request.getKeyword()))
                .and(hasCategory(request.getCategoryId()))
                .and(hasStatus(request.getStatus()))
                .and(hasVisibility(request.getVisibility()))
                .and(hasAuthor(request.getAuthorId()))
                .and(hasReviewer(request.getReviewerId()))
                .and(createdBefore(request.getCreatedBefore()))
                .and(createdAfter(request.getCreatedAfter()));
    }
}