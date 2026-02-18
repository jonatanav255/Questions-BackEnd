package com.questions.backend.dto;

import com.questions.backend.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * DTO for creating and updating questions.
 * Separates the API request structure from the entity structure.
 */
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    @Size(max = 1000, message = "Question text must not exceed 1000 characters")
    private String question;

    @NotBlank(message = "Answer text is required")
    @Size(max = 5000, message = "Answer text must not exceed 5000 characters")
    private String answer;

    @Size(max = 10000, message = "Code snippet must not exceed 10000 characters")
    private String codeSnippet;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficulty;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private List<String> tags;

    // Constructors
    public QuestionRequest() {
    }

    public QuestionRequest(String question, String answer, String codeSnippet,
                          DifficultyLevel difficulty, UUID categoryId, List<String> tags) {
        this.question = question;
        this.answer = answer;
        this.codeSnippet = codeSnippet;
        this.difficulty = difficulty;
        this.categoryId = categoryId;
        this.tags = tags;
    }

    // Getters and Setters
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
