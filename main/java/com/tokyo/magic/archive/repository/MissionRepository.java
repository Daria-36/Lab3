package com.tokyo.magic.archive.repository;

import com.tokyo.magic.archive.domain.Mission;
import com.tokyo.magic.archive.domain.MissionOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    Optional<Mission> findByMissionCode(String missionCode);

    Optional<Mission> findWithDetailsById(Long id);

    List<Mission> findByOutcomeOrderByMissionDateDesc(MissionOutcome outcome);

    List<Mission> findByMissionCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrderByMissionDateDesc(String code, String location);
}
