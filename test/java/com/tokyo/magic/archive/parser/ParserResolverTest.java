package com.tokyo.magic.archive.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokyo.magic.archive.dto.MissionPayload;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParserResolverTest {
    private final ParserResolver resolver = new ParserResolver(List.of(
            new JsonMissionParser(new ObjectMapper()),
            new YamlMissionParser(),
            new XmlMissionParser(),
            new EventLogMissionParser(),
            new PlainTextMissionParser()
    ));

    @Test
    void parsesJsonMissionFromLegacyShape() {
        String json = """
                {
                  "missionId": "M-2024-017",
                  "date": "2024-10-12",
                  "location": "Токио, район Сибуя",
                  "outcome": "SUCCESS",
                  "damageCost": 1200000,
                  "curse": { "name": "Проклятие подземного перехода", "threatLevel": "HIGH" },
                  "sorcerers": [{ "name": "Итадори Юдзи", "rank": "GRADE_1" }],
                  "techniques": [{ "name": "Черная вспышка", "type": "INNATE", "owner": "Итадори Юдзи", "damage": 500000 }]
                }
                """;

        MissionInputParser parser = resolver.resolve("mission.json", json);
        MissionPayload payload = parser.parse("mission.json", json);

        assertThat(parser.formatName()).isEqualTo("JSON");
        assertThat(payload.missionCode()).isEqualTo("M-2024-017");
        assertThat(payload.sorcerers()).hasSize(1);
        assertThat(payload.techniques()).hasSize(1);
    }

    @Test
    void parsesEventLogMissionWithoutExtension() {
        String events = """
                MISSION_CREATED|M-2024-031|2024-12-02|Токио, район Асакуса
                CURSE_DETECTED|Проклятие красных фонарей|HIGH
                SORCERER_ASSIGNED|Маки Дзэнин|GRADE_2
                TECHNIQUE_USED|Разрушительный удар|BODY|Тодо Аой|650000
                CIVILIAN_IMPACT|evacuated=43|injured=2|missing=0
                MISSION_RESULT|SUCCESS|damageCost=2100000
                """;

        MissionInputParser parser = resolver.resolve("Mission A5", events);
        MissionPayload payload = parser.parse("Mission A5", events);

        assertThat(parser.formatName()).isEqualTo("EVENT_LOG");
        assertThat(payload.missionCode()).isEqualTo("M-2024-031");
        assertThat(payload.damageCost()).isEqualTo(2_100_000L);
        assertThat(payload.civilianImpact().evacuated()).isEqualTo(43);
    }
}
