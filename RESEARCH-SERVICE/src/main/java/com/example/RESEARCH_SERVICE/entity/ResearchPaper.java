package com.example.RESEARCH_SERVICE.entity;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "research_papers")
public class ResearchPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String abstractText;

    @Column(length = 3000)
    private String keywords;

    private Long authorId;

    private String authorName;

    private String authorEmail;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ResearchCategory category;

    @Enumerated(EnumType.STRING)
    private ResearchStatus status;

    @Enumerated(EnumType.STRING)
    private ResearchVisibility visibility;

    private Integer versionNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime publishedAt;
}