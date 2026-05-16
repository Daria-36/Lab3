package com.tokyo.magic.archive.dto;

import com.tokyo.magic.archive.domain.MissionOutcome;

public record MissionSummaryResponse(
        Long id,
        String missionCode,
        String date,
        String location,
        MissionOutcome outcome,
        String outcomeTitle,
        Long damageCost,
        String curseName,
        String threatLevel,
        int sorcererCount,
        int techniqueCount
) {
}
