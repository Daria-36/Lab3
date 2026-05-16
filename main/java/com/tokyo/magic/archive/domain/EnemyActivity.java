package com.tokyo.magic.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mission_enemy_activities")
public class EnemyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    private String behaviorType;
    private String targetPriority;
    private String mobility;
    private String escalationRisk;

    @ElementCollection
    @CollectionTable(name = "enemy_attack_patterns", joinColumns = @JoinColumn(name = "enemy_activity_id"))
    @OrderColumn(name = "position")
    @Column(name = "pattern", length = 500)
    private List<String> attackPatterns = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "enemy_countermeasures", joinColumns = @JoinColumn(name = "enemy_activity_id"))
    @OrderColumn(name = "position")
    @Column(name = "countermeasure", length = 500)
    private List<String> countermeasuresUsed = new ArrayList<>();

    public Long getId() { return id; }
    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }
    public String getBehaviorType() { return behaviorType; }
    public void setBehaviorType(String behaviorType) { this.behaviorType = behaviorType; }
    public String getTargetPriority() { return targetPriority; }
    public void setTargetPriority(String targetPriority) { this.targetPriority = targetPriority; }
    public String getMobility() { return mobility; }
    public void setMobility(String mobility) { this.mobility = mobility; }
    public String getEscalationRisk() { return escalationRisk; }
    public void setEscalationRisk(String escalationRisk) { this.escalationRisk = escalationRisk; }
    public List<String> getAttackPatterns() { return attackPatterns; }
    public void setAttackPatterns(List<String> attackPatterns) { this.attackPatterns = attackPatterns == null ? new ArrayList<>() : attackPatterns; }
    public List<String> getCountermeasuresUsed() { return countermeasuresUsed; }
    public void setCountermeasuresUsed(List<String> countermeasuresUsed) { this.countermeasuresUsed = countermeasuresUsed == null ? new ArrayList<>() : countermeasuresUsed; }
}
