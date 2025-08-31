package com.sparta.match_scheduler.infrastructure.repository;

import com.sparta.match_scheduler.domain.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaTeamRepository extends JpaRepository<TeamEntity, Long> {
    boolean existsByName(String name);
    Optional<TeamEntity> findByName(String name);
}
