package com.tokyo.magic.archive.parser;

final class ParserSupport {
    private ParserSupport() {
    }

    static String extension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase();
    }

    static String firstMeaningfulCharacters(String content) {
        return content == null ? "" : content.stripLeading();
    }
}
