package com.sparta.match_scheduler.infrastructure.repository;


import com.sparta.match_scheduler.domain.MatchDetail;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepositoryCustom {
    List<MatchDetail> findMatchDetailsByDate(LocalDate date);
}

