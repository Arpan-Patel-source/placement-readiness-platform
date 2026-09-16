package com.majorproject.backend.coding;

public enum CodingCategory {
    ARRAYS("Arrays"),
    STRINGS("Strings"),
    LINKED_LIST("Linked List"),
    TREES("Trees"),
    GRAPHS("Graphs"),
    DP("Dynamic Programming"),
    GREEDY("Greedy");

    private final String displayName;

    CodingCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
