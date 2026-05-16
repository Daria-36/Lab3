package com.tokyo.magic.archive.parser;

import com.tokyo.magic.archive.domain.MissionOutcome;
import com.tokyo.magic.archive.dto.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(30)
public class XmlMissionParser implements MissionInputParser {
    @Override
    public boolean supports(String fileName, String content) {
        return ParserSupport.extension(fileName).equals("xml") || ParserSupport.firstMeaningfulCharacters(content).startsWith("<");
    }

    @Override
    public MissionPayload parse(String fileName, String content) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            document.getDocumentElement().normalize();

            CursePayload curse = null;
            Element curseElement = firstElement(document.getDocumentElement(), "curse");
            if (curseElement != null) {
                curse = new CursePayload(text(curseElement, "name"), text(curseElement, "threatLevel"));
            }

            List<SorcererPayload> sorcerers = new ArrayList<>();
            NodeList sorcererNodes = document.getElementsByTagName("sorcerer");
            for (int i = 0; i < sorcererNodes.getLength(); i++) {
                Element s = (Element) sorcererNodes.item(i);
                sorcerers.add(new SorcererPayload(text(s, "name"), text(s, "rank")));
            }

            List<TechniquePayload> techniques = new ArrayList<>();
            NodeList techniqueNodes = document.getElementsByTagName("technique");
            for (int i = 0; i < techniqueNodes.getLength(); i++) {
                Element t = (Element) techniqueNodes.item(i);
                techniques.add(new TechniquePayload(text(t, "name"), text(t, "type"), text(t, "owner"), parseLong(text(t, "damage"))));
            }

            EconomicAssessmentPayload economicAssessment = null;
            Element economic = firstElement(document.getDocumentElement(), "economicAssessment");
            if (economic != null) {
                economicAssessment = new EconomicAssessmentPayload(
                        parseLong(text(economic, "totalDamageCost")),
                        parseLong(text(economic, "infrastructureDamage")),
                        parseLong(text(economic, "commercialDamage")),
                        parseLong(text(economic, "transportDamage")),
                        parseInteger(text(economic, "recoveryEstimateDays")),
                        Boolean.parseBoolean(nullToEmpty(text(economic, "insuranceCovered")))
                );
            }

            EnemyActivityPayload enemyActivity = null;
            Element enemy = firstElement(document.getDocumentElement(), "enemyActivity");
            if (enemy != null) {
                enemyActivity = new EnemyActivityPayload(
                        text(enemy, "behaviorType"),
                        text(enemy, "targetPriority"),
                        text(enemy, "mobility"),
                        text(enemy, "escalationRisk"),
                        repeatedText(enemy, "pattern"),
                        repeatedText(enemy, "measure")
                );
            }

            EnvironmentConditionsPayload environmentConditions = null;
            Element environment = firstElement(document.getDocumentElement(), "environmentConditions");
            if (environment == null) {
                environment = firstElement(document.getDocumentElement(), "environment");
            }
            if (environment != null) {
                environmentConditions = new EnvironmentConditionsPayload(
                        text(environment, "weather"),
                        text(environment, "timeOfDay"),
                        text(environment, "visibility"),
                        parseInteger(text(environment, "cursedEnergyDensity"))
                );
            }

            CivilianImpactPayload civilianImpact = null;
            Element civilian = firstElement(document.getDocumentElement(), "civilianImpact");
            if (civilian != null) {
                civilianImpact = new CivilianImpactPayload(
                        parseInteger(text(civilian, "evacuated")),
                        parseInteger(text(civilian, "injured")),
                        parseInteger(text(civilian, "missing")),
                        text(civilian, "publicExposureRisk")
                );
            }

            return new MissionPayload(
                    text(document.getDocumentElement(), "missionId"),
                    text(document.getDocumentElement(), "date"),
                    text(document.getDocumentElement(), "location"),
                    MissionOutcome.fromString(text(document.getDocumentElement(), "outcome")),
                    parseLong(text(document.getDocumentElement(), "damageCost")),
                    text(document.getDocumentElement(), "comment"),
                    curse,
                    sorcerers,
                    techniques,
                    economicAssessment,
                    enemyActivity,
                    environmentConditions,
                    civilianImpact
            );
        } catch (Exception e) {
            throw new MissionParseException("Некорректный XML-файл миссии", e);
        }
    }

    private Element firstElement(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) {
            return null;
        }
        return (Element) list.item(0);
    }

    private String text(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0 || list.item(0).getTextContent() == null) {
            return null;
        }
        return list.item(0).getTextContent().trim();
    }

    private List<String> repeatedText(Element parent, String tag) {
        List<String> values = new ArrayList<>();
        NodeList list = parent.getElementsByTagName(tag);
        for (int i = 0; i < list.getLength(); i++) {
            String value = list.item(i).getTextContent();
            if (value != null && !value.isBlank()) {
                values.add(value.trim());
            }
        }
        return values;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.parseLong(value.trim());
    }

    private Integer parseInteger(String value) {
        Long parsed = parseLong(value);
        return parsed == null ? null : parsed.intValue();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String formatName() {
        return "XML";
    }
}
