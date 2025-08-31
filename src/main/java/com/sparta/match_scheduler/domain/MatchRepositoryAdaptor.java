package com.sparta.match_scheduler.domain;

import com.sparta.match_scheduler.infrastructure.repository.JpaMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchRepositoryAdaptor implements MatchRepository{

    private final JpaMatchRepository jpaMatchRepository;

    @Override
    public List<MatchDetail> findMatchDetailsByDate(LocalDate date) {
        return jpaMatchRepository.findMatchDetailsByDate(date);
    }

    @Override
    public void save(MatchEntity matchEntity) {
        jpaMatchRepository.save(matchEntity);
    }

    @Override
    public MatchEntity findByIdOrElseThrow(Long id) {
        return jpaMatchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found: " + id));
    }


}
