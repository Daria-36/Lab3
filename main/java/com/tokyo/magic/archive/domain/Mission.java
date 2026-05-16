package com.tokyo.magic.archive.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missions", uniqueConstraints = @UniqueConstraint(name = "uk_mission_code", columnNames = "mission_code"))
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_code", nullable = false, unique = true, length = 80)
    private String missionCode;

    @Column(name = "mission_date")
    private LocalDate missionDate;

    @Column(nullable = false, length = 300)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MissionOutcome outcome = MissionOutcome.IN_PROGRESS;

    @Column(name = "damage_cost")
    private Long damageCost = 0L;

    @Column(length = 5000)
    private String comment;

    @Embedded
    private CurseInfo curse = new CurseInfo();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Sorcerer> sorcerers = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Technique> techniques = new ArrayList<>();

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private EconomicAssessment economicAssessment;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnemyActivity enemyActivity;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private EnvironmentConditions environmentConditions;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private CivilianImpact civilianImpact;

    public Long getId() {
        return id;
    }

    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public LocalDate getMissionDate() {
        return missionDate;
    }

    public void setMissionDate(LocalDate missionDate) {
        this.missionDate = missionDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public MissionOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(MissionOutcome outcome) {
        this.outcome = outcome == null ? MissionOutcome.IN_PROGRESS : outcome;
    }

    public Long getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(Long damageCost) {
        this.damageCost = damageCost == null ? 0L : damageCost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CurseInfo getCurse() {
        return curse;
    }

    public void setCurse(CurseInfo curse) {
        this.curse = curse == null ? new CurseInfo() : curse;
    }

    public List<Sorcerer> getSorcerers() {
        return sorcerers;
    }

    public List<Technique> getTechniques() {
        return techniques;
    }

    public EconomicAssessment getEconomicAssessment() {
        return economicAssessment;
    }

    public void setEconomicAssessment(EconomicAssessment economicAssessment) {
        this.economicAssessment = economicAssessment;
        if (economicAssessment != null) {
            economicAssessment.setMission(this);
        }
    }

    public EnemyActivity getEnemyActivity() {
        return enemyActivity;
    }

    public void setEnemyActivity(EnemyActivity enemyActivity) {
        this.enemyActivity = enemyActivity;
        if (enemyActivity != null) {
            enemyActivity.setMission(this);
        }
    }

    public EnvironmentConditions getEnvironmentConditions() {
        return environmentConditions;
    }

    public void setEnvironmentConditions(EnvironmentConditions environmentConditions) {
        this.environmentConditions = environmentConditions;
        if (environmentConditions != null) {
            environmentConditions.setMission(this);
        }
    }

    public CivilianImpact getCivilianImpact() {
        return civilianImpact;
    }

    public void setCivilianImpact(CivilianImpact civilianImpact) {
        this.civilianImpact = civilianImpact;
        if (civilianImpact != null) {
            civilianImpact.setMission(this);
        }
    }

    public void addSorcerer(Sorcerer sorcerer) {
        sorcerer.setMission(this);
        sorcerers.add(sorcerer);
    }

    public void addTechnique(Technique technique) {
        technique.setMission(this);
        techniques.add(technique);
    }

    public void clearChildren() {
        sorcerers.forEach(s -> s.setMission(null));
        techniques.forEach(t -> t.setMission(null));
        sorcerers.clear();
        techniques.clear();
        setEconomicAssessment(null);
        setEnemyActivity(null);
        setEnvironmentConditions(null);
        setCivilianImpact(null);
    }
}
