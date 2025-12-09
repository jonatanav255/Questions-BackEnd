package com.questions.backend.service;

import com.questions.backend.entity.Category;
import com.questions.backend.exception.DuplicateResourceException;
import com.questions.backend.exception.ResourceConflictException;
import com.questions.backend.exception.ResourceNotFoundException;
import com.questions.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Category entity.
 * Handles business logic, validation, and coordinates between controller and repository.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories.
     *
     * @return list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return the category
     * @throws ResourceNotFoundException if category not found
     */
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    /**
     * Create a new category.
     *
     * @param category the category to create
     * @return the created category
     * @throws DuplicateResourceException if a category with the same name already exists
     */
    public Category createCategory(Category category) {
        // Check if category with this name already exists
        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateResourceException("Category", "name", category.getName());
        }

        // Initialize question count to 0 if not set
        if (category.getQuestionCount() == null) {
            category.setQuestionCount(0);
        }

        return categoryRepository.save(category);
    }

    /**
     * Update an existing category.
     *
     * @param id the category ID
     * @param updatedCategory the updated category data
     * @return the updated category
     * @throws ResourceNotFoundException if category not found
     * @throws DuplicateResourceException if name conflict with another category
     */
    public Category updateCategory(UUID id, Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check if the new name conflicts with another category
        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            if (categoryRepository.existsByName(updatedCategory.getName())) {
                throw new DuplicateResourceException("Category", "name", updatedCategory.getName());
            }
        }

        // Update fields
        existingCategory.setName(updatedCategory.getName());
        existingCategory.setColor(updatedCategory.getColor());
        existingCategory.setIcon(updatedCategory.getIcon());

        return categoryRepository.save(existingCategory);
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID
     * @throws ResourceNotFoundException if category not found
     * @throws ResourceConflictException if category has questions (cannot delete)
     */
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Prevent deletion if category has questions
        if (category.getQuestionCount() > 0) {
            throw new ResourceConflictException(
                    "Cannot delete category '" + category.getName() + "' because it contains "
                    + category.getQuestionCount() + " question(s)"
            );
        }

        categoryRepository.deleteById(id);
    }

    /**
     * Increment the question count for a category.
     * Called when a question is added to this category.
     *
     * @param categoryId the category ID
     * @throws ResourceNotFoundException if category not found
     */
    public void incrementQuestionCount(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        category.setQuestionCount(category.getQuestionCount() + 1);
        categoryRepository.save(category);
    }

    /**
     * Decrement the question count for a category.
     * Called when a question is removed from this category.
     *
     * @param categoryId the category ID
     * @throws ResourceNotFoundException if category not found
     */
    public void decrementQuestionCount(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (category.getQuestionCount() > 0) {
            category.setQuestionCount(category.getQuestionCount() - 1);
            categoryRepository.save(category);
        }
    }
}
