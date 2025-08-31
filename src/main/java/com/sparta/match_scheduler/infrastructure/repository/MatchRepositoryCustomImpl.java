package com.sparta.match_scheduler.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.match_scheduler.domain.MatchDetail;
import com.sparta.match_scheduler.domain.QMatchEntity;
import com.sparta.match_scheduler.domain.QStadiumEntity;
import com.sparta.match_scheduler.domain.QTeamEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class MatchRepositoryCustomImpl implements MatchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MatchDetail> findMatchDetailsByDate(LocalDate targetDate) {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();
        QTeamEntity awayTeam = new QTeamEntity("awayTeam");
        QTeamEntity homeTeam = new QTeamEntity("homeTeam");

        return queryFactory
                .select(Projections.constructor(MatchDetail.class,
                        QMatchEntity.matchEntity.id.as("matchId"),
                        awayTeam.id.as("awayTeamId"),
                        awayTeam.name.as("awayTeamName"),
                        awayTeam.teamCode.as("awayTeamCode"),
                        homeTeam.id.as("homeTeamId"),
                        homeTeam.name.as("homeTeamName"),
                        homeTeam.teamCode.as("homeTeamCode"),
                        QMatchEntity.matchEntity.awayScore,
                        QMatchEntity.matchEntity.homeScore,
                        QMatchEntity.matchEntity.matchTime,
                        QStadiumEntity.stadiumEntity.id.as("stadiumId"),
                        QStadiumEntity.stadiumEntity.name.as("stadiumName")
                ))
                .from(QMatchEntity.matchEntity)
                .join(QMatchEntity.matchEntity.awayTeam, awayTeam)
                .join(QMatchEntity.matchEntity.homeTeam, homeTeam)
                .join(QMatchEntity.matchEntity.stadium, QStadiumEntity.stadiumEntity)
                .where(
                        QMatchEntity.matchEntity.matchTime.gt(startOfDay)
                                .and(QMatchEntity.matchEntity.matchTime.lt(endOfDay))
                )
                .fetch();

    }
}
