package com.tokyo.magic.archive.parser;

import com.tokyo.magic.archive.dto.MissionPayload;

public interface MissionInputParser {
    boolean supports(String fileName, String content);
    MissionPayload parse(String fileName, String content);
    String formatName();
}
