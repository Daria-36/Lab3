package com.tokyo.magic.archive.service;

import com.tokyo.magic.archive.domain.*;
import com.tokyo.magic.archive.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionMapper {
    public MissionSummaryResponse toSummary(Mission mission) {
        return new MissionSummaryResponse(
                mission.getId(),
                mission.getMissionCode(),
                mission.getMissionDate() == null ? null : mission.getMissionDate().toString(),
                mission.getLocation(),
                mission.getOutcome(),
                mission.getOutcome().getDisplayName(),
                mission.getDamageCost(),
                mission.getCurse() == null ? null : mission.getCurse().getName(),
                mission.getCurse() == null ? null : mission.getCurse().getThreatLevel(),
                mission.getSorcerers().size(),
                mission.getTechniques().size()
        );
    }

    public MissionDetailsResponse toDetails(Mission mission) {
        return new MissionDetailsResponse(
                mission.getId(),
                mission.getMissionCode(),
                mission.getMissionDate() == null ? null : mission.getMissionDate().toString(),
                mission.getLocation(),
                mission.getOutcome(),
                mission.getOutcome().getDisplayName(),
                mission.getDamageCost(),
                mission.getComment(),
                mission.getCurse() == null ? null : new CursePayload(mission.getCurse().getName(), mission.getCurse().getThreatLevel()),
                mission.getSorcerers().stream().map(s -> new SorcererPayload(s.getName(), s.getRank())).toList(),
                mission.getTechniques().stream().map(t -> new TechniquePayload(t.getName(), t.getType(), t.getOwner(), t.getDamage())).toList(),
                toEconomic(mission.getEconomicAssessment()),
                toEnemy(mission.getEnemyActivity()),
                toEnvironment(mission.getEnvironmentConditions()),
                toCivilian(mission.getCivilianImpact())
        );
    }

    public void apply(MissionPayload payload, Mission mission) {
        mission.setMissionCode(payload.missionCode());
        mission.setMissionDate(payload.date() == null ? null : java.time.LocalDate.parse(payload.date()));
        mission.setLocation(payload.location());
        mission.setOutcome(payload.outcome());
        mission.setDamageCost(payload.damageCost());
        mission.setComment(payload.comment());
        mission.setCurse(payload.curse() == null ? new CurseInfo() : new CurseInfo(payload.curse().name(), payload.curse().threatLevel()));

        mission.clearChildren();

        emptyIfNull(payload.sorcerers()).forEach(s -> mission.addSorcerer(new Sorcerer(s.name(), s.rank())));
        emptyIfNull(payload.techniques()).forEach(t -> mission.addTechnique(new Technique(t.name(), t.type(), t.owner(), t.damage())));

        if (payload.economicAssessment() != null) {
            EconomicAssessment entity = new EconomicAssessment();
            entity.setTotalDamageCost(payload.economicAssessment().totalDamageCost());
            entity.setInfrastructureDamage(payload.economicAssessment().infrastructureDamage());
            entity.setCommercialDamage(payload.economicAssessment().commercialDamage());
            entity.setTransportDamage(payload.economicAssessment().transportDamage());
            entity.setRecoveryEstimateDays(payload.economicAssessment().recoveryEstimateDays());
            entity.setInsuranceCovered(payload.economicAssessment().insuranceCovered());
            mission.setEconomicAssessment(entity);
        }
        if (payload.enemyActivity() != null) {
            EnemyActivity entity = new EnemyActivity();
            entity.setBehaviorType(payload.enemyActivity().behaviorType());
            entity.setTargetPriority(payload.enemyActivity().targetPriority());
            entity.setMobility(payload.enemyActivity().mobility());
            entity.setEscalationRisk(payload.enemyActivity().escalationRisk());
            entity.setAttackPatterns(emptyIfNull(payload.enemyActivity().attackPatterns()));
            entity.setCountermeasuresUsed(emptyIfNull(payload.enemyActivity().countermeasuresUsed()));
            mission.setEnemyActivity(entity);
        }
        if (payload.environmentConditions() != null) {
            EnvironmentConditions entity = new EnvironmentConditions();
            entity.setWeather(payload.environmentConditions().weather());
            entity.setTimeOfDay(payload.environmentConditions().timeOfDay());
            entity.setVisibility(payload.environmentConditions().visibility());
            entity.setCursedEnergyDensity(payload.environmentConditions().cursedEnergyDensity());
            mission.setEnvironmentConditions(entity);
        }
        if (payload.civilianImpact() != null) {
            CivilianImpact entity = new CivilianImpact();
            entity.setEvacuated(payload.civilianImpact().evacuated());
            entity.setInjured(payload.civilianImpact().injured());
            entity.setMissing(payload.civilianImpact().missing());
            entity.setPublicExposureRisk(payload.civilianImpact().publicExposureRisk());
            mission.setCivilianImpact(entity);
        }
    }

    private EconomicAssessmentPayload toEconomic(EconomicAssessment economic) {
        if (economic == null) {
            return null;
        }
        return new EconomicAssessmentPayload(
                economic.getTotalDamageCost(),
                economic.getInfrastructureDamage(),
                economic.getCommercialDamage(),
                economic.getTransportDamage(),
                economic.getRecoveryEstimateDays(),
                economic.getInsuranceCovered()
        );
    }

    private EnemyActivityPayload toEnemy(EnemyActivity enemy) {
        if (enemy == null) {
            return null;
        }
        return new EnemyActivityPayload(
                enemy.getBehaviorType(),
                enemy.getTargetPriority(),
                enemy.getMobility(),
                enemy.getEscalationRisk(),
                List.copyOf(enemy.getAttackPatterns()),
                List.copyOf(enemy.getCountermeasuresUsed())
        );
    }

    private EnvironmentConditionsPayload toEnvironment(EnvironmentConditions environment) {
        if (environment == null) {
            return null;
        }
        return new EnvironmentConditionsPayload(
                environment.getWeather(),
                environment.getTimeOfDay(),
                environment.getVisibility(),
                environment.getCursedEnergyDensity()
        );
    }

    private CivilianImpactPayload toCivilian(CivilianImpact civilian) {
        if (civilian == null) {
            return null;
        }
        return new CivilianImpactPayload(
                civilian.getEvacuated(),
                civilian.getInjured(),
                civilian.getMissing(),
                civilian.getPublicExposureRisk()
        );
    }

    private <T> List<T> emptyIfNull(List<T> value) {
        return value == null ? List.of() : value;
    }
}
