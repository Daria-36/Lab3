package com.tokyo.magic.archive.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokyo.magic.archive.dto.MissionPayload;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Order(10)
public class JsonMissionParser implements MissionInputParser {
    private final ObjectMapper objectMapper;
    private final MissionMapReader mapReader = new MissionMapReader();

    public JsonMissionParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String fileName, String content) {
        return ParserSupport.extension(fileName).equals("json") || ParserSupport.firstMeaningfulCharacters(content).startsWith("{");
    }

    @Override
    public MissionPayload parse(String fileName, String content) {
        try {
            Map<String, Object> data = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
            return mapReader.read(data);
        } catch (Exception e) {
            throw new MissionParseException("Некорректный JSON-файл миссии", e);
        }
    }

    @Override
    public String formatName() {
        return "JSON";
    }
}
