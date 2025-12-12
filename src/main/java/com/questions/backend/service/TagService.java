package com.questions.backend.service;

import com.questions.backend.entity.Tag;
import com.questions.backend.exception.DuplicateResourceException;
import com.questions.backend.exception.ResourceNotFoundException;
import com.questions.backend.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Tag entity.
 * Handles business logic for tag operations including duplicate prevention.
 */
@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Get all tags ordered by name.
     *
     * @return list of all tags
     */
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    /**
     * Get a tag by ID.
     *
     * @param id the tag ID
     * @return the tag
     * @throws ResourceNotFoundException if tag not found
     */
    @Transactional(readOnly = true)
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    }

    /**
     * Get a tag by name (case-insensitive).
     *
     * @param name the tag name
     * @return the tag
     * @throws ResourceNotFoundException if tag not found
     */
    @Transactional(readOnly = true)
    public Tag getTagByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "name", name));
    }

    /**
     * Create a new tag.
     * Tag names are normalized to lowercase to ensure consistency.
     *
     * @param tag the tag to create
     * @return the created tag
     * @throws DuplicateResourceException if a tag with the same name already exists (case-insensitive)
     */
    public Tag createTag(Tag tag) {
        // Normalize tag name to lowercase for consistency
        String normalizedName = tag.getName().trim().toLowerCase();

        // Check if tag with this name already exists (case-insensitive)
        if (tagRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("Tag", "name", normalizedName);
        }

        tag.setName(normalizedName);
        return tagRepository.save(tag);
    }

    /**
     * Get or create a tag by name.
     * This is useful when creating questions with tags - if tag exists, return it; otherwise create it.
     *
     * @param name the tag name
     * @return existing tag or newly created tag
     */
    public Tag getOrCreateTag(String name) {
        String normalizedName = name.trim().toLowerCase();

        return tagRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    Tag newTag = new Tag(normalizedName);
                    return tagRepository.save(newTag);
                });
    }

    /**
     * Update an existing tag.
     *
     * @param id the tag ID
     * @param updatedTag the updated tag data
     * @return the updated tag
     * @throws ResourceNotFoundException if tag not found
     * @throws DuplicateResourceException if name conflict with another tag
     */
    public Tag updateTag(UUID id, Tag updatedTag) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));

        String normalizedNewName = updatedTag.getName().trim().toLowerCase();

        // Check if the new name conflicts with another tag
        if (!existingTag.getName().equalsIgnoreCase(normalizedNewName)) {
            if (tagRepository.existsByNameIgnoreCase(normalizedNewName)) {
                throw new DuplicateResourceException("Tag", "name", normalizedNewName);
            }
        }

        existingTag.setName(normalizedNewName);
        return tagRepository.save(existingTag);
    }

    /**
     * Delete a tag by ID.
     * Note: In a production system, you might want to check if the tag is used by any questions
     * before allowing deletion, similar to how Category deletion works.
     *
     * @param id the tag ID
     * @throws ResourceNotFoundException if tag not found
     */
    public void deleteTag(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag", "id", id);
        }
        tagRepository.deleteById(id);
    }
}
