package com.sparta.match_scheduler.infrastructure.repository;

import com.sparta.match_scheduler.domain.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMatchRepository extends JpaRepository<MatchEntity, Long>, MatchRepositoryCustom {
    
}
