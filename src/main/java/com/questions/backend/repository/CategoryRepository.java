package com.questions.backend.repository;

import com.questions.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Category entity.
 * Spring Data JPA automatically provides implementations for basic CRUD operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Find a category by its name.
     * Useful for checking if a category already exists before creating a new one.
     *
     * @param name the category name
     * @return Optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Check if a category with the given name already exists.
     *
     * @param name the category name
     * @return true if category exists, false otherwise
     */
    boolean existsByName(String name);
}
