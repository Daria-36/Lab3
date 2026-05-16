package com.tokyo.magic.archive.service;

import com.tokyo.magic.archive.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final MissionService missionService;
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);

    public ReportService(MissionService missionService) {
        this.missionService = missionService;
    }

    public String generate(Long missionId, ReportType type) {
        Mission mission = missionService.getMissionWithDetails(missionId);
        ReportType actualType = type == null ? ReportType.DETAILED : type;
        return switch (actualType) {
            case SUMMARY -> summary(mission);
            case RISK -> risk(mission);
            case DETAILED -> detailed(mission);
        };
    }

    public String reportFileName(Long missionId, ReportType type) {
        Mission mission = missionService.getMissionWithDetails(missionId);
        String code = mission.getMissionCode().replaceAll("[^A-Za-z0-9_-]", "_");
        return code + "_" + (type == null ? ReportType.DETAILED : type).name().toLowerCase() + "_report.txt";
    }

    private String detailed(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(72)).append("\n");
        sb.append("ДЕТАЛИЗИРОВАННЫЙ АРХИВНЫЙ ОТЧЁТ О МИССИИ\n");
        sb.append("=".repeat(72)).append("\n\n");
        appendMainInfo(sb, mission);
        appendCurse(sb, mission);
        appendSorcerers(sb, mission);
        appendTechniques(sb, mission);
        appendEconomic(sb, mission);
        appendEnemy(sb, mission);
        appendEnvironment(sb, mission);
        appendCivilian(sb, mission);
        appendComment(sb, mission);
        sb.append("\n").append("=".repeat(72)).append("\n");
        return sb.toString();
    }

    private String summary(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(56)).append("\n");
        sb.append("КРАТКИЙ АРХИВНЫЙ ОТЧЁТ\n");
        sb.append("=".repeat(56)).append("\n");
        sb.append("ID: ").append(mission.getMissionCode()).append("\n");
        sb.append("Дата: ").append(value(mission.getMissionDate())).append("\n");
        sb.append("Место: ").append(value(mission.getLocation())).append("\n");
        sb.append("Результат: ").append(mission.getOutcome().getDisplayName()).append("\n");
        if (mission.getCurse() != null && mission.getCurse().getName() != null) {
            sb.append("Проклятие: ").append(mission.getCurse().getName()).append("\n");
        }
        sb.append("Участников: ").append(mission.getSorcerers().size()).append("\n");
        sb.append("Техник: ").append(mission.getTechniques().size()).append("\n");
        sb.append("Ущерб: ").append(money(mission.getDamageCost())).append("\n");
        sb.append("=".repeat(56)).append("\n");
        return sb.toString();
    }

    private String risk(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(56)).append("\n");
        sb.append("ОТЧЁТ ПО РИСКАМ\n");
        sb.append("=".repeat(56)).append("\n\n");
        sb.append("Миссия: ").append(mission.getMissionCode()).append("\n");
        String threatLevel = mission.getCurse() == null ? "НЕ УКАЗАН" : value(mission.getCurse().getThreatLevel());
        sb.append("[1] Уровень угрозы проклятия: ").append(threatLevel).append("\n");
        sb.append("[2] Результат миссии: ").append(mission.getOutcome().getDisplayName());
        if (mission.getOutcome() == MissionOutcome.FAILURE) {
            sb.append(" — высокий риск повторного инцидента");
        }
        sb.append("\n");
        long damage = mission.getDamageCost() == null ? 0L : mission.getDamageCost();
        sb.append("[3] Экономический риск: ").append(economicRisk(damage)).append("\n");
        if (mission.getEnemyActivity() != null) {
            sb.append("[4] Активность противника: ").append(value(mission.getEnemyActivity().getBehaviorType())).append("\n");
            if (!mission.getEnemyActivity().getAttackPatterns().isEmpty()) {
                sb.append("    Паттерны атак: ").append(String.join("; ", mission.getEnemyActivity().getAttackPatterns())).append("\n");
            }
        }
        if (mission.getCivilianImpact() != null) {
            int affected = mission.getCivilianImpact().getInjured() + mission.getCivilianImpact().getMissing();
            sb.append("[5] Гражданский риск: ").append(affected > 0 ? "ПОВЫШЕННЫЙ" : "НИЗКИЙ").append("\n");
        }
        sb.append("\n").append("=".repeat(56)).append("\n");
        return sb.toString();
    }

    private void appendMainInfo(StringBuilder sb, Mission mission) {
        sb.append("ID миссии: ").append(mission.getMissionCode()).append("\n");
        sb.append("Дата: ").append(value(mission.getMissionDate())).append("\n");
        sb.append("Локация: ").append(value(mission.getLocation())).append("\n");
        sb.append("Результат: ").append(mission.getOutcome().getDisplayName()).append("\n");
        sb.append("Ущерб: ").append(money(mission.getDamageCost())).append("\n");
    }

    private void appendCurse(StringBuilder sb, Mission mission) {
        if (mission.getCurse() == null || mission.getCurse().getName() == null) {
            return;
        }
        sb.append("\nПРОКЛЯТИЕ\n").append("-".repeat(44)).append("\n");
        sb.append("Название: ").append(mission.getCurse().getName()).append("\n");
        sb.append("Уровень: ").append(value(mission.getCurse().getThreatLevel())).append("\n");
    }

    private void appendSorcerers(StringBuilder sb, Mission mission) {
        if (mission.getSorcerers().isEmpty()) {
            return;
        }
        sb.append("\nУЧАСТНИКИ\n").append("-".repeat(44)).append("\n");
        mission.getSorcerers().forEach(s -> sb.append("- ").append(s.getName()).append(" (").append(value(s.getRank())).append(")\n"));
    }

    private void appendTechniques(StringBuilder sb, Mission mission) {
        if (mission.getTechniques().isEmpty()) {
            return;
        }
        sb.append("\nПРИМЕНЕНИЯ ТЕХНИК\n").append("-".repeat(44)).append("\n");
        mission.getTechniques().forEach(t -> sb.append("- ").append(t.getName())
                .append(" | тип: ").append(value(t.getType()))
                .append(" | владелец: ").append(value(t.getOwner()))
                .append(" | урон: ").append(money(t.getDamage())).append("\n"));
    }

    private void appendEconomic(StringBuilder sb, Mission mission) {
        EconomicAssessment e = mission.getEconomicAssessment();
        if (e == null) {
            return;
        }
        sb.append("\nЭКОНОМИЧЕСКАЯ ОЦЕНКА\n").append("-".repeat(44)).append("\n");
        sb.append("Общий ущерб: ").append(money(e.getTotalDamageCost())).append("\n");
        sb.append("Инфраструктура: ").append(money(e.getInfrastructureDamage())).append("\n");
        sb.append("Коммерческий ущерб: ").append(money(e.getCommercialDamage())).append("\n");
        sb.append("Транспорт: ").append(money(e.getTransportDamage())).append("\n");
        sb.append("Оценка восстановления: ").append(e.getRecoveryEstimateDays()).append(" дней\n");
        sb.append("Покрыто страховкой: ").append(Boolean.TRUE.equals(e.getInsuranceCovered()) ? "да" : "нет").append("\n");
    }

    private void appendEnemy(StringBuilder sb, Mission mission) {
        EnemyActivity e = mission.getEnemyActivity();
        if (e == null) {
            return;
        }
        sb.append("\nАКТИВНОСТЬ ПРОТИВНИКА\n").append("-".repeat(44)).append("\n");
        sb.append("Поведение: ").append(value(e.getBehaviorType())).append("\n");
        sb.append("Приоритет цели: ").append(value(e.getTargetPriority())).append("\n");
        sb.append("Мобильность: ").append(value(e.getMobility())).append("\n");
        sb.append("Риск эскалации: ").append(value(e.getEscalationRisk())).append("\n");
        if (!e.getAttackPatterns().isEmpty()) {
            sb.append("Паттерны атак:\n");
            e.getAttackPatterns().forEach(p -> sb.append("  * ").append(p).append("\n"));
        }
        if (!e.getCountermeasuresUsed().isEmpty()) {
            sb.append("Контрмеры:\n");
            e.getCountermeasuresUsed().forEach(p -> sb.append("  * ").append(p).append("\n"));
        }
    }

    private void appendEnvironment(StringBuilder sb, Mission mission) {
        EnvironmentConditions e = mission.getEnvironmentConditions();
        if (e == null) {
            return;
        }
        sb.append("\nУСЛОВИЯ СРЕДЫ\n").append("-".repeat(44)).append("\n");
        sb.append("Погода: ").append(value(e.getWeather())).append("\n");
        sb.append("Время суток: ").append(value(e.getTimeOfDay())).append("\n");
        sb.append("Видимость: ").append(value(e.getVisibility())).append("\n");
        sb.append("Плотность проклятой энергии: ").append(e.getCursedEnergyDensity()).append("\n");
    }

    private void appendCivilian(StringBuilder sb, Mission mission) {
        CivilianImpact e = mission.getCivilianImpact();
        if (e == null) {
            return;
        }
        sb.append("\nВОЗДЕЙСТВИЕ НА ГРАЖДАНСКИХ\n").append("-".repeat(44)).append("\n");
        sb.append("Эвакуировано: ").append(e.getEvacuated()).append("\n");
        sb.append("Пострадавшие: ").append(e.getInjured()).append("\n");
        sb.append("Пропавшие: ").append(e.getMissing()).append("\n");
        sb.append("Риск раскрытия: ").append(value(e.getPublicExposureRisk())).append("\n");
    }

    private void appendComment(StringBuilder sb, Mission mission) {
        if (mission.getComment() != null && !mission.getComment().isBlank()) {
            sb.append("\nКОММЕНТАРИЙ / ХРОНОЛОГИЯ\n").append("-".repeat(44)).append("\n");
            sb.append(mission.getComment()).append("\n");
        }
    }

    private String economicRisk(long damage) {
        if (damage > 5_000_000L) return "КРИТИЧЕСКИЙ";
        if (damage > 2_000_000L) return "ВЫСОКИЙ";
        if (damage > 0L) return "СРЕДНИЙ";
        return "НИЗКИЙ";
    }

    private String money(Long value) {
        return numberFormat.format(value == null ? 0L : value) + " йен";
    }

    private String value(Object value) {
        return value == null ? "не указано" : value.toString();
    }
}
