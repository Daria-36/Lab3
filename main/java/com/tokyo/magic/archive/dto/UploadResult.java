package com.tokyo.magic.archive.dto;

public record UploadResult(
        String status,
        String parser,
        MissionDetailsResponse mission
) {
}
