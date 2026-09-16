package com.majorproject.backend.techtraining;

public enum TechCategory {
    OOP("Object-Oriented Programming", "Classes, inheritance, polymorphism, encapsulation, abstraction, and design principles."),
    DBMS("Database Management Systems", "SQL queries, normalization, transactions, indexing, ER modeling, and relational algebra."),
    OPERATING_SYSTEMS("Operating Systems", "Process management, scheduling, memory management, deadlocks, file systems, and synchronization."),
    COMPUTER_NETWORKS("Computer Networks", "OSI/TCP-IP layers, protocols, routing, subnetting, DNS, HTTP, and network security."),
    DSA("Data Structures & Algorithms", "Arrays, linked lists, trees, graphs, sorting, searching, dynamic programming, and complexity analysis."),
    WEB_TECHNOLOGIES("Web Technologies", "HTML/CSS/JS fundamentals, REST APIs, HTTP methods, cookies, sessions, and frontend/backend architecture.");

    private final String displayName;
    private final String description;

    TechCategory(String displayName, String description) {
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
