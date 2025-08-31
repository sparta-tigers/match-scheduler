package com.sparta.match_scheduler.domain;

public interface StadiumRepository {
    StadiumEntity findByNameOrElseThrow(String name);
}
