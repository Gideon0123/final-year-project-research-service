package com.example.RESEARCH_SERVICE.entity;

import com.example.RESEARCH_SERVICE.enums.ResearchStatus;
import com.example.RESEARCH_SERVICE.enums.ResearchVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "research_papers")
public class ResearchPaper extends BaseEntity {

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

    private String institution;

    private String faculty;

    private String department;

    private String fileName;

    private String contentType;

    private String storageKey;

    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ResearchCategory category;

    @Enumerated(EnumType.STRING)
    private ResearchStatus status;

    @Enumerated(EnumType.STRING)
    private ResearchVisibility visibility;

    private Integer versionNumber;

    private LocalDateTime submittedAt;

    private LocalDateTime publishedAt;

    private Long reviewerId;

    private LocalDateTime reviewAssignedAt;

    private LocalDateTime reviewCompletedAt;

    private Long downloadCount;

    private Long viewCount;
}