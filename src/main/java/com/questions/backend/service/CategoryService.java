package com.questions.backend.service;

import com.questions.backend.entity.Category;
import com.questions.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
     * @return Optional containing the category if found
     */
    public Optional<Category> getCategoryById(UUID id) {
        return categoryRepository.findById(id);
    }

    /**
     * Create a new category.
     *
     * @param category the category to create
     * @return the created category
     * @throws IllegalArgumentException if a category with the same name already exists
     */
    public Category createCategory(Category category) {
        // Check if category with this name already exists
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
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
     * @throws IllegalArgumentException if category not found or name conflict
     */
    public Category updateCategory(UUID id, Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Check if the new name conflicts with another category
        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            if (categoryRepository.existsByName(updatedCategory.getName())) {
                throw new IllegalArgumentException("Category with name '" + updatedCategory.getName() + "' already exists");
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
     * @throws IllegalArgumentException if category not found
     * @throws IllegalStateException if category has questions (cannot delete)
     */
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Prevent deletion if category has questions
        if (category.getQuestionCount() > 0) {
            throw new IllegalStateException(
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
     */
    public void incrementQuestionCount(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        category.setQuestionCount(category.getQuestionCount() + 1);
        categoryRepository.save(category);
    }

    /**
     * Decrement the question count for a category.
     * Called when a question is removed from this category.
     *
     * @param categoryId the category ID
     */
    public void decrementQuestionCount(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        if (category.getQuestionCount() > 0) {
            category.setQuestionCount(category.getQuestionCount() - 1);
            categoryRepository.save(category);
        }
    }
}
