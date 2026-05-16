package com.tokyo.magic.archive.service;

import com.tokyo.magic.archive.domain.Mission;
import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.MissionDetailsResponse;
import com.tokyo.magic.archive.dto.MissionPayload;
import com.tokyo.magic.archive.dto.MissionSummaryResponse;
import com.tokyo.magic.archive.dto.UploadResult;
import com.tokyo.magic.archive.parser.MissionInputParser;
import com.tokyo.magic.archive.parser.ParserResolver;
import com.tokyo.magic.archive.repository.MissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MissionService {
    private final MissionRepository missionRepository;
    private final ParserResolver parserResolver;
    private final MissionNormalizer normalizer;
    private final MissionValidator validator;
    private final MissionMapper mapper;

    public MissionService(MissionRepository missionRepository,
                          ParserResolver parserResolver,
                          MissionNormalizer normalizer,
                          MissionValidator validator,
                          MissionMapper mapper) {
        this.missionRepository = missionRepository;
        this.parserResolver = parserResolver;
        this.normalizer = normalizer;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Transactional
    public UploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new MissionValidationException("Файл миссии не выбран");
        }
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            MissionInputParser parser = parserResolver.resolve(file.getOriginalFilename(), content);
            MissionPayload parsed = parser.parse(file.getOriginalFilename(), content);
            MissionDetailsResponse saved = save(parsed);
            return new UploadResult("SAVED", parser.formatName(), saved);
        } catch (IOException e) {
            throw new MissionValidationException("Не удалось прочитать файл миссии");
        }
    }

    @Transactional
    public MissionDetailsResponse save(MissionPayload payload) {
        MissionPayload normalized = normalizer.normalize(payload);
        validator.validate(normalized);

        Mission mission = missionRepository.findByMissionCode(normalized.missionCode())
                .orElseGet(Mission::new);
        mapper.apply(normalized, mission);
        Mission saved = missionRepository.save(mission);
        return mapper.toDetails(saved);
    }

    public List<MissionSummaryResponse> findAll(String query, MissionOutcome outcome) {
        List<Mission> missions;
        if (outcome != null) {
            missions = missionRepository.findByOutcomeOrderByMissionDateDesc(outcome);
        } else if (query != null && !query.isBlank()) {
            missions = missionRepository.findByMissionCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrderByMissionDateDesc(query, query);
        } else {
            missions = missionRepository.findAll();
        }
        return missions.stream().map(mapper::toSummary).toList();
    }

    public MissionDetailsResponse getDetails(Long id) {
        Mission mission = getMissionWithDetails(id);
        return mapper.toDetails(mission);
    }

    public Mission getMissionWithDetails(Long id) {
        return missionRepository.findWithDetailsById(id).orElseThrow(() -> new MissionNotFoundException(id));
    }
}
