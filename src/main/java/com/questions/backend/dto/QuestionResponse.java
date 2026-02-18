package com.questions.backend.dto;

import com.questions.backend.entity.Category;
import com.questions.backend.entity.DifficultyLevel;
import com.questions.backend.entity.Question;
import com.questions.backend.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for question responses.
 * Provides a clean API response structure with denormalized data.
 */
public class QuestionResponse {

    private UUID id;
    private String question;
    private String answer;
    private String codeSnippet;
    private DifficultyLevel difficulty;
    private UUID categoryId;
    private String categoryName;
    private String categoryColor;
    private List<String> tags;
    private LocalDateTime createdAt;

    // Constructors
    public QuestionResponse() {
    }

    /**
     * Create a QuestionResponse from a Question entity.
     *
     * @param question the question entity
     * @return the response DTO
     */
    public static QuestionResponse fromEntity(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestion(question.getQuestion());
        response.setAnswer(question.getAnswer());
        response.setCodeSnippet(question.getCodeSnippet());
        response.setDifficulty(question.getDifficulty());
        response.setCreatedAt(question.getCreatedAt());

        // Category data
        Category category = question.getCategory();
        if (category != null) {
            response.setCategoryId(category.getId());
            response.setCategoryName(category.getName());
            response.setCategoryColor(category.getColor());
        }

        // Tags as list of strings
        response.setTags(
            question.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList())
        );

        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
