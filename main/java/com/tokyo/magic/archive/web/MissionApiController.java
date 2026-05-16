package com.tokyo.magic.archive.web;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.domain.ReportType;
import com.tokyo.magic.archive.dto.MissionDetailsResponse;
import com.tokyo.magic.archive.dto.MissionPayload;
import com.tokyo.magic.archive.dto.MissionSummaryResponse;
import com.tokyo.magic.archive.dto.UploadResult;
import com.tokyo.magic.archive.service.MissionService;
import com.tokyo.magic.archive.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "Missions", description = "Загрузка миссий, архив и отчеты")
public class MissionApiController {
    private final MissionService missionService;
    private final ReportService reportService;

    public MissionApiController(MissionService missionService, ReportService reportService) {
        this.missionService = missionService;
        this.reportService = reportService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить файл миссии", description = "Принимает JSON, YAML, XML, TXT или event-log файл, нормализует данные и сохраняет миссию в БД.")
    public UploadResult upload(@RequestParam("file") MultipartFile file) {
        return missionService.upload(file);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Создать или обновить миссию через JSON")
    public MissionDetailsResponse save(@RequestBody MissionPayload payload) {
        return missionService.save(payload);
    }

    @GetMapping
    @Operation(summary = "Получить архив миссий")
    public List<MissionSummaryResponse> findAll(@RequestParam(required = false) String q,
                                                @RequestParam(required = false) MissionOutcome outcome) {
        return missionService.findAll(q, outcome);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить полную запись миссии")
    public MissionDetailsResponse getOne(@PathVariable Long id) {
        return missionService.getDetails(id);
    }

    @GetMapping(value = "/{id}/report", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Сформировать отчет по миссии")
    public String report(@PathVariable Long id, @RequestParam(defaultValue = "DETAILED") ReportType type) {
        return reportService.generate(id, type);
    }

    @GetMapping(value = "/{id}/report/download", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Скачать отчет по миссии как txt-файл")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id, @RequestParam(defaultValue = "DETAILED") ReportType type) {
        String report = reportService.generate(id, type);
        String fileName = reportService.reportFileName(id, type);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.TEXT_PLAIN)
                .body(report.getBytes(StandardCharsets.UTF_8));
    }
}
