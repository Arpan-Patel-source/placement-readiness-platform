package com.majorproject.backend.mockinterview;

public enum InterviewRoundType {
    HR("HR Round"),
    TECHNICAL("Technical Round"),
    CODING("Coding Round");

    private final String displayName;

    InterviewRoundType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
