package com.tokyo.magic.archive.dto;

import java.util.List;

public record EnemyActivityPayload(
        String behaviorType,
        String targetPriority,
        String mobility,
        String escalationRisk,
        List<String> attackPatterns,
        List<String> countermeasuresUsed
) {
}
