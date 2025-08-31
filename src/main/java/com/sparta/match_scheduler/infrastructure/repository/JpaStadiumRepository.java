package com.sparta.match_scheduler.infrastructure.repository;

import com.sparta.match_scheduler.domain.StadiumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaStadiumRepository extends JpaRepository<StadiumEntity, Long> {
    boolean existsByName(String name);
    Optional<StadiumEntity> findByName(String name);
}
