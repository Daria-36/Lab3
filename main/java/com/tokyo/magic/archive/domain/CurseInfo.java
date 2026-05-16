package com.tokyo.magic.archive.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CurseInfo {
    @Column(name = "curse_name")
    private String name;

    @Column(name = "curse_threat_level")
    private String threatLevel;

    public CurseInfo() {
    }

    public CurseInfo(String name, String threatLevel) {
        this.name = name;
        this.threatLevel = threatLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel) {
        this.threatLevel = threatLevel;
    }
}
