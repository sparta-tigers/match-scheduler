package com.sparta.match_scheduler.domain;

import com.sparta.match_scheduler.infrastructure.repository.JpaTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryAdaptor implements TeamRepository{

    private final JpaTeamRepository teamRepository;

    @Override
    public TeamEntity findByNameOrElseThrow(String name) {
        return teamRepository.findByName(name).orElseThrow(() -> new RuntimeException("Team not found: " + name));
    }
}
