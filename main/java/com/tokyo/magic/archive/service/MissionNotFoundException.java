package com.tokyo.magic.archive.service;

public class MissionNotFoundException extends RuntimeException {
    public MissionNotFoundException(Long id) {
        super("Миссия с id=" + id + " не найдена");
    }
}
