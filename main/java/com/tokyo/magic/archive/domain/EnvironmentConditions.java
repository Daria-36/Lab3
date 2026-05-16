package com.tokyo.magic.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "mission_environment_conditions")
public class EnvironmentConditions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    private String weather;
    private String timeOfDay;
    private String visibility;
    private Integer cursedEnergyDensity = 0;

    public Long getId() { return id; }
    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public Integer getCursedEnergyDensity() { return cursedEnergyDensity; }
    public void setCursedEnergyDensity(Integer cursedEnergyDensity) { this.cursedEnergyDensity = cursedEnergyDensity == null ? 0 : cursedEnergyDensity; }
}
