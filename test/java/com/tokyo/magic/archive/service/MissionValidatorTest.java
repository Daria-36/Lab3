package com.tokyo.magic.archive.service;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.MissionPayload;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MissionValidatorTest {
    private final MissionValidator validator = new MissionValidator();

    @Test
    void rejectsMissionWithoutCode() {
        MissionPayload payload = new MissionPayload(null, "2024-10-12", "Токио", MissionOutcome.SUCCESS, 0L, null, null, List.of(), List.of(), null, null, null, null);

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(MissionValidationException.class)
                .hasMessageContaining("missionId");
    }

    @Test
    void rejectsBrokenDate() {
        MissionPayload payload = new MissionPayload("M-1", "12.10.2024", "Токио", MissionOutcome.SUCCESS, 0L, null, null, List.of(), List.of(), null, null, null, null);

        assertThatThrownBy(() -> validator.validate(payload))
                .isInstanceOf(MissionValidationException.class)
                .hasMessageContaining("YYYY-MM-DD");
    }
}
