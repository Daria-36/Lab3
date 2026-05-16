package com.tokyo.magic.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_techniques")
public class Technique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 80)
    private String type;

    @Column(length = 180)
    private String owner;

    @Column(nullable = false)
    private Long damage = 0L;

    public Technique() {
    }

    public Technique(String name, String type, String owner, Long damage) {
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.damage = damage == null ? 0L : damage;
    }

    public Long getId() {
        return id;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getDamage() {
        return damage;
    }

    public void setDamage(Long damage) {
        this.damage = damage == null ? 0L : damage;
    }
}
