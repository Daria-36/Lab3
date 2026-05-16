package com.tokyo.magic.archive.parser;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParserResolver {
    private final List<MissionInputParser> parsers;

    public ParserResolver(List<MissionInputParser> parsers) {
        this.parsers = parsers;
    }

    public MissionInputParser resolve(String fileName, String content) {
        return parsers.stream()
                .filter(parser -> parser.supports(fileName, content))
                .findFirst()
                .orElseThrow(() -> new MissionParseException("Не удалось определить формат входных данных"));
    }
}
