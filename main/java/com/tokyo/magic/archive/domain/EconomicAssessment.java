package com.tokyo.magic.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_economic_assessments")
public class EconomicAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    private Long totalDamageCost = 0L;
    private Long infrastructureDamage = 0L;
    private Long commercialDamage = 0L;
    private Long transportDamage = 0L;
    private Integer recoveryEstimateDays = 0;
    private Boolean insuranceCovered = false;

    public Long getId() { return id; }
    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }
    public Long getTotalDamageCost() { return totalDamageCost; }
    public void setTotalDamageCost(Long totalDamageCost) { this.totalDamageCost = totalDamageCost == null ? 0L : totalDamageCost; }
    public Long getInfrastructureDamage() { return infrastructureDamage; }
    public void setInfrastructureDamage(Long infrastructureDamage) { this.infrastructureDamage = infrastructureDamage == null ? 0L : infrastructureDamage; }
    public Long getCommercialDamage() { return commercialDamage; }
    public void setCommercialDamage(Long commercialDamage) { this.commercialDamage = commercialDamage == null ? 0L : commercialDamage; }
    public Long getTransportDamage() { return transportDamage; }
    public void setTransportDamage(Long transportDamage) { this.transportDamage = transportDamage == null ? 0L : transportDamage; }
    public Integer getRecoveryEstimateDays() { return recoveryEstimateDays; }
    public void setRecoveryEstimateDays(Integer recoveryEstimateDays) { this.recoveryEstimateDays = recoveryEstimateDays == null ? 0 : recoveryEstimateDays; }
    public Boolean getInsuranceCovered() { return insuranceCovered; }
    public void setInsuranceCovered(Boolean insuranceCovered) { this.insuranceCovered = insuranceCovered == null ? false : insuranceCovered; }
}
