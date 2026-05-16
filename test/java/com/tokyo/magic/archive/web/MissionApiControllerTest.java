package com.tokyo.magic.archive.web;

import com.tokyo.magic.archive.repository.MissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MissionApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MissionRepository missionRepository;

    @BeforeEach
    void cleanDatabase() {
        missionRepository.deleteAll();
    }

    @Test
    void createsMissionAndGeneratesReport() throws Exception {
        String request = """
                {
                  "missionCode": "M-API-1",
                  "date": "2024-10-12",
                  "location": "Токио",
                  "outcome": "SUCCESS",
                  "damageCost": 1200000,
                  "curse": { "name": "Тестовое проклятие", "threatLevel": "HIGH" },
                  "sorcerers": [{ "name": "Итадори Юдзи", "rank": "GRADE_1" }],
                  "techniques": [{ "name": "Черная вспышка", "type": "INNATE", "owner": "Итадори Юдзи", "damage": 500000 }]
                }
                """;

        String response = mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.missionCode").value("M-API-1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/missions/" + id + "/report").param("type", "DETAILED"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ДЕТАЛИЗИРОВАННЫЙ")))
                .andExpect(content().string(containsString("M-API-1")))
                .andExpect(content().string(containsString("Тестовое проклятие")));
    }
}
