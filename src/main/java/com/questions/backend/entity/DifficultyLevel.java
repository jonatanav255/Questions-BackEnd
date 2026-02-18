package com.questions.backend.entity;

/**
 * Enum representing the difficulty levels for questions.
 * Used to categorize questions by their complexity.
 */
public enum DifficultyLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    SENIOR("Senior");

    private final String displayName;

    DifficultyLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convert string to DifficultyLevel, case-insensitive.
     * @param value the string value to convert
     * @return the corresponding DifficultyLevel
     * @throws IllegalArgumentException if the value doesn't match any difficulty level
     */
    public static DifficultyLevel fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Difficulty level cannot be null");
        }

        for (DifficultyLevel level : DifficultyLevel.values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }

        throw new IllegalArgumentException(
            "Invalid difficulty level: " + value + ". Valid values are: BEGINNER, INTERMEDIATE, SENIOR"
        );
    }
}
