package com.tokyo.magic.archive.web;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.domain.ReportType;
import com.tokyo.magic.archive.dto.UploadResult;
import com.tokyo.magic.archive.service.MissionService;
import com.tokyo.magic.archive.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UiController {
    private final MissionService missionService;
    private final ReportService reportService;

    public UiController(MissionService missionService, ReportService reportService) {
        this.missionService = missionService;
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String q,
                        @RequestParam(required = false) MissionOutcome outcome,
                        Model model) {
        model.addAttribute("missions", missionService.findAll(q, outcome));
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("selectedOutcome", outcome);
        model.addAttribute("outcomes", MissionOutcome.values());
        return "index";
    }

    @PostMapping("/missions/upload")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        UploadResult result = missionService.upload(file);
        redirectAttributes.addFlashAttribute("success", "Миссия " + result.mission().missionCode() + " сохранена. Формат: " + result.parser());
        return "redirect:/";
    }

    @GetMapping("/missions/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("mission", missionService.getDetails(id));
        model.addAttribute("reportTypes", ReportType.values());
        return "mission";
    }

    @GetMapping("/missions/{id}/report")
    public String report(@PathVariable Long id,
                         @RequestParam(defaultValue = "DETAILED") ReportType type,
                         Model model) {
        model.addAttribute("mission", missionService.getDetails(id));
        model.addAttribute("selectedType", type);
        model.addAttribute("reportTypes", ReportType.values());
        model.addAttribute("report", reportService.generate(id, type));
        return "report";
    }
}
