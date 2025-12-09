package com.questions.backend.controller;

import com.questions.backend.entity.Category;
import com.questions.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Category API.
 * Handles HTTP requests for category operations.
 *
 * Exception handling is done globally by GlobalExceptionHandler,
 * so this controller only needs to call service methods.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/categories
     * Get all categories.
     *
     * @return list of all categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/categories/{id}
     * Get a single category by ID.
     *
     * @param id the category ID
     * @return the category
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found (handled by GlobalExceptionHandler)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * POST /api/categories
     * Create a new category.
     *
     * @param category the category to create
     * @return the created category with 201 Created status
     * @throws com.questions.backend.exception.DuplicateResourceException if name already exists (handled by GlobalExceptionHandler)
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /**
     * PUT /api/categories/{id}
     * Update an existing category.
     *
     * @param id the category ID
     * @param category the updated category data
     * @return the updated category
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found (handled by GlobalExceptionHandler)
     * @throws com.questions.backend.exception.DuplicateResourceException if name conflict (handled by GlobalExceptionHandler)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * DELETE /api/categories/{id}
     * Delete a category.
     *
     * @param id the category ID
     * @return 204 No Content if successful
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found (handled by GlobalExceptionHandler)
     * @throws com.questions.backend.exception.ResourceConflictException if category has questions (handled by GlobalExceptionHandler)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
