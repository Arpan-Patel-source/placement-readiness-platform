package com.majorproject.backend.hrtraining;

public enum HrCategory {
    SELF_INTRODUCTION("Self-Introduction & Background", "Elevator pitch, professional narrative, and foundational strengths."),
    LEADERSHIP("Leadership & Initiative", "Stepping up, taking ownership, decision-making, and motivating peers."),
    CONFLICT_RESOLUTION("Conflict Resolution", "Navigating disagreements, handling friction, and professional diplomacy."),
    TEAMWORK("Collaboration & Teamwork", "Cross-functional synergy, peer support, and collective accountability."),
    FAILURE_RESILIENCE("Overcoming Failure & Resilience", "Learning from mistakes, managing stress, and turnaround execution."),
    CAREER_VISION("Career Vision & Cultural Fit", "Long-term ambition, company alignment, and professional purpose.");

    private final String title;
    private final String description;

    HrCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
