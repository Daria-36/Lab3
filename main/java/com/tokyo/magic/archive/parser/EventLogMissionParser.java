package com.tokyo.magic.archive.parser;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(40)
public class EventLogMissionParser implements MissionInputParser {
    @Override
    public boolean supports(String fileName, String content) {
        String trimmed = ParserSupport.firstMeaningfulCharacters(content);
        return trimmed.startsWith("MISSION_CREATED|") || trimmed.contains("\nMISSION_CREATED|");
    }

    @Override
    public MissionPayload parse(String fileName, String content) {
        String missionCode = null;
        String date = null;
        String location = null;
        MissionOutcome outcome = MissionOutcome.IN_PROGRESS;
        Long damageCost = 0L;
        CursePayload curse = null;
        List<SorcererPayload> sorcerers = new ArrayList<>();
        List<TechniquePayload> techniques = new ArrayList<>();
        List<String> attackPatterns = new ArrayList<>();
        List<String> countermeasures = new ArrayList<>();
        List<String> timeline = new ArrayList<>();
        CivilianImpactPayload civilianImpact = null;

        for (String rawLine : content.split("\\R")) {
            if (rawLine.isBlank()) {
                continue;
            }
            String[] parts = rawLine.split("\\|");
            String event = parts[0].trim();
            switch (event) {
                case "MISSION_CREATED" -> {
                    missionCode = part(parts, 1);
                    date = part(parts, 2);
                    location = part(parts, 3);
                }
                case "CURSE_DETECTED" -> curse = new CursePayload(part(parts, 1), part(parts, 2));
                case "SORCERER_ASSIGNED" -> sorcerers.add(new SorcererPayload(part(parts, 1), part(parts, 2)));
                case "TECHNIQUE_USED" -> techniques.add(new TechniquePayload(part(parts, 1), part(parts, 2), part(parts, 3), parseLong(part(parts, 4))));
                case "TIMELINE_EVENT" -> timeline.add(part(parts, 1) + " — " + part(parts, 2) + ": " + part(parts, 3));
                case "ENEMY_ACTION" -> attackPatterns.add(part(parts, 1) + ": " + part(parts, 2));
                case "COUNTERMEASURE" -> countermeasures.add(part(parts, 1));
                case "CIVILIAN_IMPACT" -> civilianImpact = parseCivilian(parts);
                case "MISSION_RESULT" -> {
                    outcome = MissionOutcome.fromString(part(parts, 1));
                    for (int i = 2; i < parts.length; i++) {
                        String[] kv = parts[i].split("=", 2);
                        if (kv.length == 2 && kv[0].equals("damageCost")) {
                            damageCost = parseLong(kv[1]);
                        }
                    }
                }
                default -> {
                    // неизвестные события не ломают импорт, но сохраняются в комментарии
                    timeline.add(rawLine);
                }
            }
        }

        EnemyActivityPayload enemyActivity = attackPatterns.isEmpty() && countermeasures.isEmpty()
                ? null
                : new EnemyActivityPayload("EVENT_LOG", null, null, null, attackPatterns, countermeasures);
        String comment = timeline.isEmpty() ? null : String.join("\n", timeline);

        return new MissionPayload(
                missionCode, date, location, outcome, damageCost, comment, curse,
                sorcerers, techniques, null, enemyActivity, null, civilianImpact
        );
    }

    private CivilianImpactPayload parseCivilian(String[] parts) {
        Integer evacuated = 0;
        Integer injured = 0;
        Integer missing = 0;
        for (int i = 1; i < parts.length; i++) {
            String[] kv = parts[i].split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            switch (kv[0]) {
                case "evacuated" -> evacuated = parseInteger(kv[1]);
                case "injured" -> injured = parseInteger(kv[1]);
                case "missing" -> missing = parseInteger(kv[1]);
                default -> {
                }
            }
        }
        return new CivilianImpactPayload(evacuated, injured, missing, null);
    }

    private String part(String[] parts, int index) {
        return index < parts.length ? parts[index].trim() : null;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        return Long.parseLong(value.trim());
    }

    private Integer parseInteger(String value) {
        return parseLong(value).intValue();
    }

    @Override
    public String formatName() {
        return "EVENT_LOG";
    }
}
