package com.questions.backend.repository;

import com.questions.backend.entity.DifficultyLevel;
import com.questions.backend.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Question entity.
 * Provides custom query methods for filtering questions by various criteria.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    /**
     * Find all questions belonging to a specific category.
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return page of questions
     */
    Page<Question> findByCategoryId(UUID categoryId, Pageable pageable);

    /**
     * Find all questions with a specific difficulty level.
     *
     * @param difficulty the difficulty level
     * @param pageable pagination information
     * @return page of questions
     */
    Page<Question> findByDifficulty(DifficultyLevel difficulty, Pageable pageable);

    /**
     * Find all questions by category and difficulty.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @param pageable pagination information
     * @return page of questions
     */
    Page<Question> findByCategoryIdAndDifficulty(UUID categoryId, DifficultyLevel difficulty, Pageable pageable);

    /**
     * Find questions by category and difficulty (non-paginated).
     * Useful for practice sessions.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @return list of questions
     */
    List<Question> findByCategoryIdAndDifficulty(UUID categoryId, DifficultyLevel difficulty);

    /**
     * Count questions by category.
     *
     * @param categoryId the category ID
     * @return number of questions in the category
     */
    long countByCategoryId(UUID categoryId);

    /**
     * Count questions by category and difficulty.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @return number of questions
     */
    long countByCategoryIdAndDifficulty(UUID categoryId, DifficultyLevel difficulty);

    /**
     * Find questions that have a specific tag.
     *
     * @param tagId the tag ID
     * @param pageable pagination information
     * @return page of questions
     */
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t.id = :tagId")
    Page<Question> findByTagId(@Param("tagId") UUID tagId, Pageable pageable);

    /**
     * Find random questions by category and difficulty.
     * Useful for generating practice sessions.
     *
     * @param categoryId the category ID
     * @param difficulty the difficulty level
     * @param limit maximum number of questions to return
     * @return list of random questions
     */
    @Query(value = "SELECT * FROM questions WHERE category_id = :categoryId AND difficulty = CAST(:difficulty AS TEXT) ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("categoryId") UUID categoryId, @Param("difficulty") String difficulty, @Param("limit") int limit);
}
