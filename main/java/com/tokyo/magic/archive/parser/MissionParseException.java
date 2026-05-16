package com.tokyo.magic.archive.parser;

public class MissionParseException extends RuntimeException {
    public MissionParseException(String message) {
        super(message);
    }

    public MissionParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
