package com.questions.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Question entity representing a Q&A flashcard.
 * Each question belongs to a category and can have multiple tags.
 */
@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_question_category", columnList = "category_id"),
        @Index(name = "idx_question_difficulty", columnList = "difficulty")
})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Question text is required")
    @Size(max = 1000, message = "Question text must not exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String question;

    @NotBlank(message = "Answer text is required")
    @Size(max = 5000, message = "Answer text must not exceed 5000 characters")
    @Column(nullable = false, length = 5000)
    private String answer;

    @Size(max = 10000, message = "Code snippet must not exceed 10000 characters")
    @Column(name = "code_snippet", length = 10000)
    private String codeSnippet;

    @NotNull(message = "Difficulty level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Question() {
    }

    public Question(String question, String answer, String codeSnippet, DifficultyLevel difficulty, Category category) {
        this.question = question;
        this.answer = answer;
        this.codeSnippet = codeSnippet;
        this.difficulty = difficulty;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", difficulty=" + difficulty +
                ", categoryId=" + (category != null ? category.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id != null && id.equals(question.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
