package com.majorproject.backend.coding;

public enum CodingLanguage {
    JAVA("Java", "java"),
    PYTHON("Python", "python"),
    CPP("C++", "cpp"),
    C("C", "c");

    private final String displayName;
    private final String fileExtension;

    CodingLanguage(String displayName, String fileExtension) {
        this.displayName = displayName;
        this.fileExtension = fileExtension;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
