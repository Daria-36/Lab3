package com.tokyo.magic.archive.dto;

import com.tokyo.magic.archive.domain.MissionOutcome;
import java.util.List;

public record MissionDetailsResponse(
        Long id,
        String missionCode,
        String date,
        String location,
        MissionOutcome outcome,
        String outcomeTitle,
        Long damageCost,
        String comment,
        CursePayload curse,
        List<SorcererPayload> sorcerers,
        List<TechniquePayload> techniques,
        EconomicAssessmentPayload economicAssessment,
        EnemyActivityPayload enemyActivity,
        EnvironmentConditionsPayload environmentConditions,
        CivilianImpactPayload civilianImpact
) {
}
