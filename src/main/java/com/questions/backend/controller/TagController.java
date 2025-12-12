package com.questions.backend.controller;

import com.questions.backend.entity.Tag;
import com.questions.backend.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Tag API.
 * Handles HTTP requests for tag operations.
 *
 * Tags are normalized to lowercase to ensure consistency.
 * Exception handling is done globally by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * GET /api/tags
     * Get all tags.
     *
     * @return list of all tags
     */
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    /**
     * GET /api/tags/{id}
     * Get a single tag by ID.
     *
     * @param id the tag ID
     * @return the tag
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable UUID id) {
        Tag tag = tagService.getTagById(id);
        return ResponseEntity.ok(tag);
    }

    /**
     * GET /api/tags/name/{name}
     * Get a tag by name (case-insensitive).
     *
     * @param name the tag name
     * @return the tag
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String name) {
        Tag tag = tagService.getTagByName(name);
        return ResponseEntity.ok(tag);
    }

    /**
     * POST /api/tags
     * Create a new tag.
     * Tag name will be normalized to lowercase.
     *
     * @param tag the tag to create
     * @return the created tag with 201 Created status
     * @throws com.questions.backend.exception.DuplicateResourceException if name already exists
     */
    @PostMapping
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    /**
     * PUT /api/tags/{id}
     * Update an existing tag.
     * Tag name will be normalized to lowercase.
     *
     * @param id the tag ID
     * @param tag the updated tag data
     * @return the updated tag
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found
     * @throws com.questions.backend.exception.DuplicateResourceException if name conflict
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(
            @PathVariable UUID id,
            @Valid @RequestBody Tag tag) {
        Tag updatedTag = tagService.updateTag(id, tag);
        return ResponseEntity.ok(updatedTag);
    }

    /**
     * DELETE /api/tags/{id}
     * Delete a tag.
     *
     * @param id the tag ID
     * @return 204 No Content if successful
     * @throws com.questions.backend.exception.ResourceNotFoundException if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
