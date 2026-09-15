package com.majorproject.backend.aptitude;

public enum AptitudeCategory {
    QUANTITATIVE("Quantitative Aptitude", "Mathematical problem-solving, numbers, arithmetic, and data interpretation."),
    LOGICAL_REASONING("Logical Reasoning", "Analytical thinking, pattern recognition, deduction, and relational logic."),
    VERBAL_ABILITY("Verbal Ability", "English comprehension, grammar, vocabulary, and verbal structuring.");

    private final String displayName;
    private final String description;

    AptitudeCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
