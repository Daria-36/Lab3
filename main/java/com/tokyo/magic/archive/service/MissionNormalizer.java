package com.tokyo.magic.archive.service;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionNormalizer {
    public MissionPayload normalize(MissionPayload payload) {
        if (payload == null) {
            throw new MissionValidationException("Миссия не передана");
        }
        Long damageCost = payload.damageCost();
        if ((damageCost == null || damageCost == 0L) && payload.economicAssessment() != null) {
            damageCost = payload.economicAssessment().totalDamageCost();
        }
        if (damageCost == null) {
            damageCost = 0L;
        }

        return new MissionPayload(
                trim(payload.missionCode()),
                trim(payload.date()),
                trim(payload.location()),
                payload.outcome() == null ? MissionOutcome.IN_PROGRESS : payload.outcome(),
                damageCost,
                trim(payload.comment()),
                normalizeCurse(payload.curse()),
                emptyIfNull(payload.sorcerers()).stream()
                        .map(s -> new SorcererPayload(trim(s.name()), trim(s.rank())))
                        .filter(s -> s.name() != null && !s.name().isBlank())
                        .toList(),
                emptyIfNull(payload.techniques()).stream()
                        .map(t -> new TechniquePayload(trim(t.name()), trim(t.type()), trim(t.owner()), t.damage() == null ? 0L : t.damage()))
                        .filter(t -> t.name() != null && !t.name().isBlank())
                        .toList(),
                payload.economicAssessment(),
                normalizeEnemy(payload.enemyActivity()),
                payload.environmentConditions(),
                payload.civilianImpact()
        );
    }

    private CursePayload normalizeCurse(CursePayload curse) {
        if (curse == null) {
            return null;
        }
        String name = trim(curse.name());
        String threatLevel = trim(curse.threatLevel());
        if (name == null && threatLevel == null) {
            return null;
        }
        return new CursePayload(name, threatLevel);
    }

    private EnemyActivityPayload normalizeEnemy(EnemyActivityPayload enemyActivity) {
        if (enemyActivity == null) {
            return null;
        }
        return new EnemyActivityPayload(
                trim(enemyActivity.behaviorType()),
                trim(enemyActivity.targetPriority()),
                trim(enemyActivity.mobility()),
                trim(enemyActivity.escalationRisk()),
                emptyIfNull(enemyActivity.attackPatterns()).stream().map(this::trim).filter(v -> v != null && !v.isBlank()).toList(),
                emptyIfNull(enemyActivity.countermeasuresUsed()).stream().map(this::trim).filter(v -> v != null && !v.isBlank()).toList()
        );
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private <T> List<T> emptyIfNull(List<T> value) {
        return value == null ? List.of() : value;
    }
}
