package com.tokyo.magic.archive.dto;

public record EconomicAssessmentPayload(
        Long totalDamageCost,
        Long infrastructureDamage,
        Long commercialDamage,
        Long transportDamage,
        Integer recoveryEstimateDays,
        Boolean insuranceCovered
) {
}
