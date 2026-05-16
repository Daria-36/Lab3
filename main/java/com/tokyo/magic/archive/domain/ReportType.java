package com.tokyo.magic.archive.domain;

public enum ReportType {
    DETAILED,
    SUMMARY,
    RISK;

    public static ReportType fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return DETAILED;
        }
        return ReportType.valueOf(value.trim().toUpperCase());
    }
}
