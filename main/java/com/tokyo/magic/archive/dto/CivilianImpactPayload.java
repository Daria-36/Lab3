package com.tokyo.magic.archive.dto;

public record CivilianImpactPayload(
        Integer evacuated,
        Integer injured,
        Integer missing,
        String publicExposureRisk
) {
}
