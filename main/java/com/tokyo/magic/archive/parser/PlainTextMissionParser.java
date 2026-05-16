package com.tokyo.magic.archive.parser;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(50)
public class PlainTextMissionParser implements MissionInputParser {
    @Override
    public boolean supports(String fileName, String content) {
        String extension = ParserSupport.extension(fileName);
        return extension.equals("txt") || content.contains("[MISSION]") || content.contains("missionId=") || content.contains("missionId:");
    }

    @Override
    public MissionPayload parse(String fileName, String content) {
        if (content.contains("[MISSION]") || content.contains("=")) {
            return parseSectionText(content);
        }
        return parseColonText(content);
    }

    private MissionPayload parseSectionText(String content) {
        Map<String, String> mission = new LinkedHashMap<>();
        Map<String, String> curse = new LinkedHashMap<>();
        Map<String, String> environment = new LinkedHashMap<>();
        Map<String, String> civilian = new LinkedHashMap<>();
        List<Map<String, String>> sorcerers = new ArrayList<>();
        List<Map<String, String>> techniques = new ArrayList<>();

        String section = "MISSION";
        Map<String, String> current = mission;

        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length() - 1).toUpperCase();
                switch (section) {
                    case "CURSE" -> current = curse;
                    case "SORCERER" -> {
                        current = new LinkedHashMap<>();
                        sorcerers.add(current);
                    }
                    case "TECHNIQUE" -> {
                        current = new LinkedHashMap<>();
                        techniques.add(current);
                    }
                    case "ENVIRONMENT", "ENVIRONMENT_CONDITIONS" -> current = environment;
                    case "CIVILIAN", "CIVILIAN_IMPACT" -> current = civilian;
                    default -> current = mission;
                }
                continue;
            }
            String[] kv = line.split("=", 2);
            if (kv.length == 2) {
                current.put(kv[0].trim(), kv[1].trim());
            }
        }

        return new MissionPayload(
                mission.get("missionId"),
                mission.get("date"),
                mission.get("location"),
                MissionOutcome.fromString(mission.get("outcome")),
                parseLong(mission.get("damageCost")),
                mission.get("comment"),
                curse.isEmpty() ? null : new CursePayload(curse.get("name"), curse.get("threatLevel")),
                sorcerers.stream().map(s -> new SorcererPayload(s.get("name"), s.get("rank"))).toList(),
                techniques.stream().map(t -> new TechniquePayload(t.get("name"), t.get("type"), t.get("owner"), parseLong(t.get("damage")))).toList(),
                null,
                null,
                environment.isEmpty() ? null : new EnvironmentConditionsPayload(environment.get("weather"), environment.get("timeOfDay"), environment.get("visibility"), parseInteger(environment.get("cursedEnergyDensity"))),
                civilian.isEmpty() ? null : new CivilianImpactPayload(parseInteger(civilian.get("evacuated")), parseInteger(civilian.get("injured")), parseInteger(civilian.get("missing")), civilian.get("publicExposureRisk"))
        );
    }

    private MissionPayload parseColonText(String content) {
        Map<Integer, Map<String, String>> sorcerers = new LinkedHashMap<>();
        Map<Integer, Map<String, String>> techniques = new LinkedHashMap<>();
        Map<String, String> mission = new LinkedHashMap<>();
        Map<String, String> curse = new LinkedHashMap<>();

        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (line.isBlank() || !line.contains(":")) {
                continue;
            }
            String[] kv = line.split(":", 2);
            String key = kv[0].trim();
            String value = kv[1].trim();
            if (key.startsWith("curse.")) {
                curse.put(key.substring("curse.".length()), value);
            } else if (key.startsWith("sorcerer[") && key.contains("].")) {
                int index = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
                String field = key.substring(key.indexOf("].") + 2);
                sorcerers.computeIfAbsent(index, ignored -> new LinkedHashMap<>()).put(field, value);
            } else if (key.startsWith("technique[") && key.contains("].")) {
                int index = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
                String field = key.substring(key.indexOf("].") + 2);
                techniques.computeIfAbsent(index, ignored -> new LinkedHashMap<>()).put(field, value);
            } else {
                mission.put(key, value);
            }
        }

        return new MissionPayload(
                mission.get("missionId"),
                mission.get("date"),
                mission.get("location"),
                MissionOutcome.fromString(mission.get("outcome")),
                parseLong(mission.get("damageCost")),
                mission.get("comment"),
                curse.isEmpty() ? null : new CursePayload(curse.get("name"), curse.get("threatLevel")),
                sorcerers.values().stream().map(s -> new SorcererPayload(s.get("name"), s.get("rank"))).toList(),
                techniques.values().stream().map(t -> new TechniquePayload(t.get("name"), t.get("type"), t.get("owner"), parseLong(t.get("damage")))).toList(),
                null, null, null, null
        );
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
        return "TXT";
    }
}
