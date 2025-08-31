package com.sparta.match_scheduler.application;

import com.sparta.match_scheduler.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public List<MatchDetail> getMatchDetails(LocalDate targetDate) {
        return matchRepository.findMatchDetailsByDate(targetDate);
    }

    public void updateMatchesFromCrawledData(List<MatchTableRow> crawledMatches, List<MatchDetail> matchDetails) {
        for(MatchTableRow crawledMatch : crawledMatches) {
            MatchDetail existingDetail = findMatchingDetail(matchDetails, crawledMatch);

            // exsitingDetail이 null이 아니면 업데이트
            if (existingDetail != null) {
                MatchResult result = crawledMatch.matchResult();
                MatchEntity entity = matchRepository.findByIdOrElseThrow(existingDetail.getMatchId());

                if (result == MatchResult.CANCEL) {

                } else if(result != MatchResult.NOT_PLAYED) {
                    entity.updateScores(crawledMatch.homeTeamScore(), crawledMatch.awayTeamScore());
                    entity.updateStatus(result);
                }

                matchRepository.save(entity);
            } else {
                // null이면 새로운 매치 데이터 생성
                TeamEntity homeTeam = teamRepository.findByNameOrElseThrow(crawledMatch.homeTeamName());
                TeamEntity awayTeam = teamRepository.findByNameOrElseThrow(crawledMatch.awayTeamName());
                StadiumEntity stadium = stadiumRepository.findByNameOrElseThrow(crawledMatch.stadiumName());
                MatchEntity newMatch = MatchEntity.builder()
                        .matchTime(crawledMatch.matchTime())
                        .homeTeam(homeTeam)
                        .awayTeam(awayTeam)
                        .stadium(stadium)
                        .awayScore(crawledMatch.awayTeamScore())
                        .homeScore(crawledMatch.homeTeamScore())
                        .matchResult(crawledMatch.matchResult())
                        .build();

                matchRepository.save(newMatch);
            }

        }
    }

    private MatchDetail findMatchingDetail(List<MatchDetail> existingMatches, MatchTableRow crawledMatch) {
        return existingMatches.stream()
                .filter(match -> isSameMatch(match, crawledMatch))
                .findFirst()
                .orElse(null);
    }

    private boolean isSameMatch(MatchDetail detail, MatchTableRow row) {
        return detail.getMatchTime().equals(row.matchTime()) &&
                detail.getAwayTeamName().equals(row.awayTeamName()) &&
                detail.getHomeTeamName().equals(row.homeTeamName());
    }

}