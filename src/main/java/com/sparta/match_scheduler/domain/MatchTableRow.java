package com.sparta.match_scheduler.domain;

import java.time.LocalDateTime;

// KBO 사이트의 경기 결과 테이블의 한 행을 나타내는 클래스
public record MatchTableRow(
    LocalDateTime matchTime,
    String awayTeamName,
    String homeTeamName,
    int awayTeamScore,
    int homeTeamScore,
    String stadiumName,
    String remark,
    boolean hasHighlight
) {
    public MatchResult matchResult() {
        if(!"-".equals(remark)) {
            return MatchResult.CANCEL;
        }

        if(hasHighlight) {
            if (homeTeamScore > awayTeamScore) {
                return MatchResult.HOME_WIN;
            } else if (homeTeamScore < awayTeamScore) {
                return MatchResult.AWAY_WIN;
            } else {
                return MatchResult.DRAW;
            }
        } else {
            return MatchResult.NOT_PLAYED;
        }
    }
}