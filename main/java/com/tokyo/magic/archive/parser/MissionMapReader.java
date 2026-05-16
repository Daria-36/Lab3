package com.tokyo.magic.archive.parser;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MissionMapReader {
    public MissionPayload read(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            throw new MissionParseException("Входные данные пустые");
        }

        String missionCode = firstText(data, "missionCode", "missionId", "id");
        String date = firstText(data, "date", "missionDate");
        String location = firstText(data, "location", "place");
        MissionOutcome outcome = MissionOutcome.fromString(firstText(data, "outcome", "result"));
        Long damageCost = firstLong(data, "damageCost", "damage", "cost");
        String comment = firstText(data, "comment", "description", "notes");

        CursePayload curse = null;
        Map<String, Object> curseMap = objectMap(data.get("curse"));
        if (curseMap != null) {
            curse = new CursePayload(firstText(curseMap, "name"), firstText(curseMap, "threatLevel", "level"));
        }

        List<SorcererPayload> sorcerers = new ArrayList<>();
        for (Map<String, Object> sorcerer : objectList(data.get("sorcerers"))) {
            sorcerers.add(new SorcererPayload(firstText(sorcerer, "name"), firstText(sorcerer, "rank", "grade")));
        }

        List<TechniquePayload> techniques = new ArrayList<>();
        for (Map<String, Object> technique : objectList(data.get("techniques"))) {
            techniques.add(new TechniquePayload(
                    firstText(technique, "name"),
                    firstText(technique, "type"),
                    firstText(technique, "owner"),
                    firstLong(technique, "damage")
            ));
        }

        EconomicAssessmentPayload economicAssessment = null;
        Map<String, Object> economicMap = objectMap(data.get("economicAssessment"));
        if (economicMap != null) {
            economicAssessment = new EconomicAssessmentPayload(
                    firstLong(economicMap, "totalDamageCost"),
                    firstLong(economicMap, "infrastructureDamage"),
                    firstLong(economicMap, "commercialDamage"),
                    firstLong(economicMap, "transportDamage"),
                    firstInteger(economicMap, "recoveryEstimateDays"),
                    firstBoolean(economicMap, "insuranceCovered")
            );
        }

        EnemyActivityPayload enemyActivity = null;
        Map<String, Object> enemyMap = objectMap(data.get("enemyActivity"));
        if (enemyMap != null) {
            enemyActivity = new EnemyActivityPayload(
                    firstText(enemyMap, "behaviorType"),
                    firstText(enemyMap, "targetPriority"),
                    firstText(enemyMap, "mobility"),
                    firstText(enemyMap, "escalationRisk"),
                    stringList(enemyMap.get("attackPatterns")),
                    stringList(enemyMap.get("countermeasuresUsed"))
            );
        }

        EnvironmentConditionsPayload environmentConditions = null;
        Map<String, Object> environmentMap = objectMap(data.get("environmentConditions"));
        if (environmentMap == null) {
            environmentMap = objectMap(data.get("environment"));
        }
        if (environmentMap != null) {
            environmentConditions = new EnvironmentConditionsPayload(
                    firstText(environmentMap, "weather"),
                    firstText(environmentMap, "timeOfDay"),
                    firstText(environmentMap, "visibility"),
                    firstInteger(environmentMap, "cursedEnergyDensity")
            );
        }

        CivilianImpactPayload civilianImpact = null;
        Map<String, Object> civilianMap = objectMap(data.get("civilianImpact"));
        if (civilianMap != null) {
            civilianImpact = new CivilianImpactPayload(
                    firstInteger(civilianMap, "evacuated"),
                    firstInteger(civilianMap, "injured"),
                    firstInteger(civilianMap, "missing"),
                    firstText(civilianMap, "publicExposureRisk")
            );
        }

        return new MissionPayload(
                missionCode, date, location, outcome, damageCost, comment, curse,
                sorcerers, techniques, economicAssessment, enemyActivity, environmentConditions, civilianImpact
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> objectList(Object value) {
        if (value instanceof List<?> list) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof Map<?, ?>) {
                    result.add((Map<String, Object>) item);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    private List<String> stringList(Object value) {
        if (value instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object item : list) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    private String firstText(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value instanceof java.time.LocalDate localDate) {
                return localDate.toString();
            }
            if (value instanceof java.util.Date date) {
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
            }
            if (value != null && !value.toString().isBlank()) {
                return value.toString().trim();
            }
        }
        return null;
    }

    private Long firstLong(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value instanceof Number number) {
                return number.longValue();
            }
            if (value != null && !value.toString().isBlank()) {
                try {
                    return Long.parseLong(value.toString().replace("_", "").trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private Integer firstInteger(Map<String, Object> map, String... keys) {
        Long value = firstLong(map, keys);
        return value == null ? null : value.intValue();
    }

    private Boolean firstBoolean(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value instanceof Boolean bool) {
                return bool;
            }
            if (value != null && !value.toString().isBlank()) {
                return Boolean.parseBoolean(value.toString());
            }
        }
        return null;
    }
}
