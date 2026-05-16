package com.tokyo.magic.archive.dto;

public record EnvironmentConditionsPayload(
        String weather,
        String timeOfDay,
        String visibility,
        Integer cursedEnergyDensity
) {
}
