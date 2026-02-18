package com.questions.backend.service;

import com.questions.backend.entity.Category;
import com.questions.backend.entity.DifficultyLevel;
import com.questions.backend.entity.Question;
import com.questions.backend.entity.Tag;
import com.questions.backend.exception.ResourceNotFoundException;
import com.questions.backend.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Question entity.
 * Handles business logic, validation, and coordinates between controller and repository.
 */
@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    public QuestionService(QuestionRepository questionRepository,
                          CategoryService categoryService,
                          TagService tagService) {
        this.questionRepository = questionRepository;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    /**
     * Get all questions with pagination.
     *
     * @param pageable pagination information
     * @return page of questions
     */
    @Transactional(readOnly = true)
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    /**
     * Get a question by ID.
     *
     * @param id the question ID
     * @return the question
     * @throws ResourceNotFoundException if question not found
     */
    @Transactional(readOnly = true)
    public Question getQuestionById(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
    }

    /**
     * Get questions by category with pagination.
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return page of questions
     */
    @Transactional(readOnly = true)
    public Page<Question> getQuestionsByCategory(UUID categoryId, Pageable pageable) {
        // Verify category exists
        categoryService.getCategoryById(categoryId);
        return questionRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Get questions by difficulty with pagination.
     *
     * @param difficulty the difficulty level
     * @param pageable pagination information
     * @return page of questions
     */
    @Transactional(readOnly = true)
    public Page<Question> getQuestionsByDifficulty(DifficultyLevel difficulty, Pageable pageable) {
        return questionRepository.findByDifficulty(difficulty, pageable);
    }

    /**
     * Get questions by category and difficulty with pagination.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @param pageable pagination information
     * @return page of questions
     */
    @Transactional(readOnly = true)
    public Page<Question> getQuestionsByCategoryAndDifficulty(UUID categoryId, DifficultyLevel difficulty, Pageable pageable) {
        // Verify category exists
        categoryService.getCategoryById(categoryId);
        return questionRepository.findByCategoryIdAndDifficulty(categoryId, difficulty, pageable);
    }

    /**
     * Get questions by tag with pagination.
     *
     * @param tagId the tag ID
     * @param pageable pagination information
     * @return page of questions
     */
    @Transactional(readOnly = true)
    public Page<Question> getQuestionsByTag(UUID tagId, Pageable pageable) {
        // Verify tag exists
        tagService.getTagById(tagId);
        return questionRepository.findByTagId(tagId, pageable);
    }

    /**
     * Get random questions for practice session.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @param limit maximum number of questions
     * @return list of random questions
     */
    @Transactional(readOnly = true)
    public List<Question> getRandomQuestions(UUID categoryId, DifficultyLevel difficulty, int limit) {
        // Verify category exists
        categoryService.getCategoryById(categoryId);

        // Validate limit
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        return questionRepository.findRandomQuestions(categoryId, difficulty.name(), limit);
    }

    /**
     * Create a new question.
     *
     * @param question the question to create
     * @param categoryId the category ID
     * @param tagNames list of tag names (will be created if they don't exist)
     * @return the created question
     * @throws ResourceNotFoundException if category not found
     */
    public Question createQuestion(Question question, UUID categoryId, List<String> tagNames) {
        // Validate and set category
        Category category = categoryService.getCategoryById(categoryId);
        question.setCategory(category);

        // Handle tags - get existing or create new ones
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = tagNames.stream()
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .map(String::trim)
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            question.setTags(tags);
        }

        // Save question
        Question savedQuestion = questionRepository.save(question);

        // Increment category question count
        categoryService.incrementQuestionCount(categoryId);

        return savedQuestion;
    }

    /**
     * Update an existing question.
     *
     * @param id the question ID
     * @param updatedQuestion the updated question data
     * @param categoryId the category ID (can be changed)
     * @param tagNames list of tag names
     * @return the updated question
     * @throws ResourceNotFoundException if question or category not found
     */
    public Question updateQuestion(UUID id, Question updatedQuestion, UUID categoryId, List<String> tagNames) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        UUID oldCategoryId = existingQuestion.getCategory().getId();
        boolean categoryChanged = !oldCategoryId.equals(categoryId);

        // Validate and update category if changed
        if (categoryChanged) {
            Category newCategory = categoryService.getCategoryById(categoryId);
            existingQuestion.setCategory(newCategory);

            // Update question counts
            categoryService.decrementQuestionCount(oldCategoryId);
            categoryService.incrementQuestionCount(categoryId);
        }

        // Update basic fields
        existingQuestion.setQuestion(updatedQuestion.getQuestion());
        existingQuestion.setAnswer(updatedQuestion.getAnswer());
        existingQuestion.setCodeSnippet(updatedQuestion.getCodeSnippet());
        existingQuestion.setDifficulty(updatedQuestion.getDifficulty());

        // Update tags
        existingQuestion.getTags().clear();
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = tagNames.stream()
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .map(String::trim)
                    .map(tagService::getOrCreateTag)
                    .collect(Collectors.toSet());
            existingQuestion.setTags(tags);
        }

        return questionRepository.save(existingQuestion);
    }

    /**
     * Delete a question by ID.
     *
     * @param id the question ID
     * @throws ResourceNotFoundException if question not found
     */
    public void deleteQuestion(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        UUID categoryId = question.getCategory().getId();

        // Delete question
        questionRepository.deleteById(id);

        // Decrement category question count
        categoryService.decrementQuestionCount(categoryId);
    }

    /**
     * Count total questions.
     *
     * @return total number of questions
     */
    @Transactional(readOnly = true)
    public long countAllQuestions() {
        return questionRepository.count();
    }

    /**
     * Count questions by category.
     *
     * @param categoryId the category ID
     * @return number of questions in the category
     */
    @Transactional(readOnly = true)
    public long countQuestionsByCategory(UUID categoryId) {
        return questionRepository.countByCategoryId(categoryId);
    }

    /**
     * Count questions by category and difficulty.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @return number of questions
     */
    @Transactional(readOnly = true)
    public long countQuestionsByCategoryAndDifficulty(UUID categoryId, DifficultyLevel difficulty) {
        return questionRepository.countByCategoryIdAndDifficulty(categoryId, difficulty);
    }
}
