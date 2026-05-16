package com.tokyo.magic.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_civilian_impacts")
public class CivilianImpact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    private Integer evacuated = 0;
    private Integer injured = 0;
    private Integer missing = 0;
    private String publicExposureRisk;

    public Long getId() { return id; }
    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }
    public Integer getEvacuated() { return evacuated; }
    public void setEvacuated(Integer evacuated) { this.evacuated = evacuated == null ? 0 : evacuated; }
    public Integer getInjured() { return injured; }
    public void setInjured(Integer injured) { this.injured = injured == null ? 0 : injured; }
    public Integer getMissing() { return missing; }
    public void setMissing(Integer missing) { this.missing = missing == null ? 0 : missing; }
    public String getPublicExposureRisk() { return publicExposureRisk; }
    public void setPublicExposureRisk(String publicExposureRisk) { this.publicExposureRisk = publicExposureRisk; }
}
