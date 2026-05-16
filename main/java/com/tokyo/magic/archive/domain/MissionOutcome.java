package com.tokyo.magic.archive.domain;

public enum MissionOutcome {
    SUCCESS("Успех"),
    FAILURE("Провал"),
    PARTIAL_SUCCESS("Частичный успех"),
    IN_PROGRESS("В процессе");

    private final String displayName;

    MissionOutcome(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static MissionOutcome fromString(String value) {
        if (value == null || value.isBlank()) {
            return IN_PROGRESS;
        }
        String normalized = value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "SUCCESS", "УСПЕХ" -> SUCCESS;
            case "FAILURE", "FAILED", "ПРОВАЛ" -> FAILURE;
            case "PARTIAL_SUCCESS", "PARTIAL", "ЧАСТИЧНЫЙ_УСПЕХ" -> PARTIAL_SUCCESS;
            default -> IN_PROGRESS;
        };
    }
}
