package com.sparta.match_scheduler.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity(name = "matches")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchEntity extends BaseEntity {
    @Id
    @Column(name = "match_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private TeamEntity homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private TeamEntity awayTeam;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private StadiumEntity stadium;

    @Column(name = "home_score", nullable = false)
    private Integer homeScore;

    @Column(name = "away_score", nullable = false)
    private Integer awayScore;

    @Column(name = "match_time", nullable = false)
    private LocalDateTime matchTime;

    @Column
    private String remark; // 비고 
    
    @Column
    @Enumerated(value = EnumType.STRING)
    private MatchResult matchResult;


    public static MatchEntity of(StadiumEntity stadium, TeamEntity homeTeam, TeamEntity awayTeam, LocalDateTime matchTime, MatchResult matchResult, Integer homeTeamScore, Integer awayTeamScore) {
        MatchEntity match = new MatchEntity();
        match.stadium = stadium;
        match.homeTeam = homeTeam;
        match.awayTeam = awayTeam;
        match.matchTime = matchTime;
        match.matchResult = matchResult;
        match.homeScore = homeTeamScore;
        match.awayScore = awayTeamScore;

        return match;
    }

    public void updateScores(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public void updateStatus(MatchResult matchResult) {
        this.matchResult = matchResult;
    }
}