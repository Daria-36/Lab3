package com.tokyo.magic.archive.parser;

import com.tokyo.magic.archive.dto.MissionPayload;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@Component
@Order(20)
public class YamlMissionParser implements MissionInputParser {
    private final MissionMapReader mapReader = new MissionMapReader();

    @Override
    public boolean supports(String fileName, String content) {
        String extension = ParserSupport.extension(fileName);
        String trimmed = ParserSupport.firstMeaningfulCharacters(content);
        return extension.equals("yaml") || extension.equals("yml")
                || (trimmed.startsWith("missionId:") || trimmed.startsWith("missionCode:"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public MissionPayload parse(String fileName, String content) {
        try {
            Object loaded = new Yaml().load(content);
            if (!(loaded instanceof Map<?, ?> map)) {
                throw new MissionParseException("YAML не содержит объект миссии");
            }
            return mapReader.read((Map<String, Object>) map);
        } catch (MissionParseException e) {
            throw e;
        } catch (Exception e) {
            throw new MissionParseException("Некорректный YAML-файл миссии", e);
        }
    }

    @Override
    public String formatName() {
        return "YAML";
    }
}
