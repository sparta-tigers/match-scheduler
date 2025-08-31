package com.sparta.match_scheduler.domain;

public interface TeamRepository {
    TeamEntity findByNameOrElseThrow(String name);
}
