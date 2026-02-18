package com.questions.backend.controller;

import com.questions.backend.dto.QuestionRequest;
import com.questions.backend.dto.QuestionResponse;
import com.questions.backend.entity.DifficultyLevel;
import com.questions.backend.entity.Question;
import com.questions.backend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Question API.
 * Handles HTTP requests for question operations.
 *
 * Exception handling is done globally by GlobalExceptionHandler,
 * so this controller only needs to call service methods.
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * GET /api/questions
     * Get all questions with pagination and optional filtering.
     *
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @param sortBy field to sort by (default createdAt)
     * @param sortDir sort direction (default DESC)
     * @param categoryId optional category filter
     * @param difficulty optional difficulty filter
     * @param tagId optional tag filter
     * @return page of questions
     */
    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) UUID tagId) {

        // Create pageable
        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Question> questions;

        // Apply filters
        if (categoryId != null && difficulty != null) {
            DifficultyLevel difficultyLevel = DifficultyLevel.fromString(difficulty);
            questions = questionService.getQuestionsByCategoryAndDifficulty(categoryId, difficultyLevel, pageable);
        } else if (categoryId != null) {
            questions = questionService.getQuestionsByCategory(categoryId, pageable);
        } else if (difficulty != null) {
            DifficultyLevel difficultyLevel = DifficultyLevel.fromString(difficulty);
            questions = questionService.getQuestionsByDifficulty(difficultyLevel, pageable);
        } else if (tagId != null) {
            questions = questionService.getQuestionsByTag(tagId, pageable);
        } else {
            questions = questionService.getAllQuestions(pageable);
        }

        // Convert to response DTOs
        Page<QuestionResponse> response = questions.map(QuestionResponse::fromEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/questions/{id}
     * Get a single question by ID.
     *
     * @param id the question ID
     * @return the question
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable UUID id) {
        Question question = questionService.getQuestionById(id);
        QuestionResponse response = QuestionResponse.fromEntity(question);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/questions/category/{categoryId}
     * Get questions by category with pagination.
     *
     * @param categoryId the category ID
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @return page of questions
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<QuestionResponse>> getQuestionsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Question> questions = questionService.getQuestionsByCategory(categoryId, pageable);
        Page<QuestionResponse> response = questions.map(QuestionResponse::fromEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/questions/category/{categoryId}/difficulty/{difficulty}
     * Get questions by category and difficulty.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @return page of questions
     */
    @GetMapping("/category/{categoryId}/difficulty/{difficulty}")
    public ResponseEntity<Page<QuestionResponse>> getQuestionsByCategoryAndDifficulty(
            @PathVariable UUID categoryId,
            @PathVariable String difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        DifficultyLevel difficultyLevel = DifficultyLevel.fromString(difficulty);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Question> questions = questionService.getQuestionsByCategoryAndDifficulty(categoryId, difficultyLevel, pageable);
        Page<QuestionResponse> response = questions.map(QuestionResponse::fromEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/questions/random
     * Get random questions for practice session.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @param limit number of questions (max 100, default 20)
     * @return list of random questions
     */
    @GetMapping("/random")
    public ResponseEntity<List<QuestionResponse>> getRandomQuestions(
            @RequestParam UUID categoryId,
            @RequestParam String difficulty,
            @RequestParam(defaultValue = "20") int limit) {

        DifficultyLevel difficultyLevel = DifficultyLevel.fromString(difficulty);
        List<Question> questions = questionService.getRandomQuestions(categoryId, difficultyLevel, limit);
        List<QuestionResponse> response = questions.stream()
                .map(QuestionResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/questions/count
     * Get question counts.
     *
     * @param categoryId optional category filter
     * @param difficulty optional difficulty filter
     * @return count of questions
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getQuestionCount(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String difficulty) {

        long count;

        if (categoryId != null && difficulty != null) {
            DifficultyLevel difficultyLevel = DifficultyLevel.fromString(difficulty);
            count = questionService.countQuestionsByCategoryAndDifficulty(categoryId, difficultyLevel);
        } else if (categoryId != null) {
            count = questionService.countQuestionsByCategory(categoryId);
        } else {
            count = questionService.countAllQuestions();
        }

        return ResponseEntity.ok(count);
    }

    /**
     * POST /api/questions
     * Create a new question.
     *
     * @param request the question request DTO
     * @return the created question with 201 Created status
     * @throws com.questions.backend.exception.ResourceNotFoundException if category not found
     */
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        // Create question entity from request
        Question question = new Question();
        question.setQuestion(request.getQuestion());
        question.setAnswer(request.getAnswer());
        question.setCodeSnippet(request.getCodeSnippet());
        question.setDifficulty(request.getDifficulty());

        // Create question
        Question createdQuestion = questionService.createQuestion(
                question,
                request.getCategoryId(),
                request.getTags()
        );

        QuestionResponse response = QuestionResponse.fromEntity(createdQuestion);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/questions/{id}
     * Update an existing question.
     *
     * @param id the question ID
     * @param request the updated question data
     * @return the updated question
     * @throws com.questions.backend.exception.ResourceNotFoundException if question or category not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequest request) {

        // Create question entity from request
        Question question = new Question();
        question.setQuestion(request.getQuestion());
        question.setAnswer(request.getAnswer());
        question.setCodeSnippet(request.getCodeSnippet());
        question.setDifficulty(request.getDifficulty());

        // Update question
        Question updatedQuestion = questionService.updateQuestion(
                id,
                question,
                request.getCategoryId(),
                request.getTags()
        );

        QuestionResponse response = QuestionResponse.fromEntity(updatedQuestion);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/questions/{id}
     * Delete a question.
     *
     * @param id the question ID
     * @return 204 No Content if successful
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
