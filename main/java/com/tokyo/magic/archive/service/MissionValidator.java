package com.tokyo.magic.archive.service;

import com.tokyo.magic.archive.dto.MissionPayload;
import com.tokyo.magic.archive.dto.TechniquePayload;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class MissionValidator {
    public void validate(MissionPayload payload) {
        if (payload.missionCode() == null || payload.missionCode().isBlank()) {
            throw new MissionValidationException("Поле missionId/missionCode обязательно");
        }
        if (payload.location() == null || payload.location().isBlank()) {
            throw new MissionValidationException("Поле location обязательно");
        }
        if (payload.date() != null && !payload.date().isBlank()) {
            try {
                LocalDate.parse(payload.date());
            } catch (DateTimeParseException e) {
                throw new MissionValidationException("Дата миссии должна быть в формате YYYY-MM-DD");
            }
        }
        if (payload.damageCost() != null && payload.damageCost() < 0) {
            throw new MissionValidationException("Ущерб не может быть отрицательным");
        }
        if (payload.techniques() != null) {
            for (TechniquePayload technique : payload.techniques()) {
                if (technique.damage() != null && technique.damage() < 0) {
                    throw new MissionValidationException("Урон техники не может быть отрицательным");
                }
            }
        }
    }
}
