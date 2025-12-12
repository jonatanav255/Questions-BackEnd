package com.questions.backend.repository;

import com.questions.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Tag entity.
 * Spring Data JPA automatically provides implementations for basic CRUD operations.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    /**
     * Find a tag by its name (case-sensitive).
     * Useful for checking if a tag already exists before creating a new one.
     *
     * @param name the tag name
     * @return Optional containing the tag if found
     */
    Optional<Tag> findByName(String name);

    /**
     * Find a tag by its name (case-insensitive).
     * Allows flexible tag matching regardless of capitalization.
     *
     * @param name the tag name
     * @return Optional containing the tag if found
     */
    Optional<Tag> findByNameIgnoreCase(String name);

    /**
     * Check if a tag with the given name already exists (case-insensitive).
     *
     * @param name the tag name
     * @return true if tag exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
