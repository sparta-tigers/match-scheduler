package com.sparta.match_scheduler.domain;

import com.sparta.match_scheduler.infrastructure.repository.JpaStadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StadiumRepositoryAdaptor implements StadiumRepository{
    private final JpaStadiumRepository stadiumRepository;

    @Override
    public StadiumEntity findByNameOrElseThrow(String name) {
        return stadiumRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Stadium not found: " + name));
    }
}
