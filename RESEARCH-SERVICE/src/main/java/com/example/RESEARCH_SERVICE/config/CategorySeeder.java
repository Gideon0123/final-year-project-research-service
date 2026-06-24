package com.example.RESEARCH_SERVICE.config;

import com.example.RESEARCH_SERVICE.entity.ResearchCategory;
import com.example.RESEARCH_SERVICE.repository.ResearchCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategorySeeder implements CommandLineRunner {

    private final ResearchCategoryRepository categoryRepository;

    @Override
    public void run(String @NonNull ... args) {

        List<String> categories = List.of(

                "Computer Science",
                "Cyber Security",
                "Artificial Intelligence",
                "Software Engineering",
                "Data Science",

                "Computer Engineering",
                "Information Technology",

                "Electrical Engineering",
                "Mechanical Engineering",

                "Medicine",
                "Agriculture",

                "Business Administration",
                "Accounting",
                "Economics",

                "Law",
                "Education",

                "Biochemistry",
                "Microbiology",

                "Physics",
                "Chemistry",
                "Mathematics",
                "Medicine",
                "Pharmacy",
                "Psychology"
        );

        for (String categoryName : categories) {

            if (!categoryRepository.existsByNameIgnoreCase(categoryName)
            ) {
                categoryRepository.save(
                        ResearchCategory.builder()
                                .name(categoryName)
                                .description(categoryName + " Research Category")
                                .build()
                );

                log.info("Inserted category: {}", categoryName);
            }
        }

        log.info("Category seeding completed");
    }
}