package com.sparta.match_scheduler.domain;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository {
    List<MatchDetail> findMatchDetailsByDate(LocalDate date);

    void save(MatchEntity matchEntity);

    MatchEntity findByIdOrElseThrow(Long id);
}
