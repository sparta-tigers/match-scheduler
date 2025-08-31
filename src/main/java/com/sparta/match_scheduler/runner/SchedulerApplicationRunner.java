package com.sparta.match_scheduler.runner;

import com.sparta.match_scheduler.application.CrawlerService;
import com.sparta.match_scheduler.application.MatchService;
import com.sparta.match_scheduler.domain.MatchDetail;
import com.sparta.match_scheduler.domain.MatchTableRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerApplicationRunner implements ApplicationRunner {

    // KST (한국 표준시) 시간대 상수
    private static final ZoneId KST_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MatchService matchService;
    private final CrawlerService crawlerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("start scheduler");
            LocalDate targetDate = parseDate(args.getOptionValues("targetDate").get(0));
            log.info("Scheduler target date: {}", targetDate);

            List<MatchDetail> matchDetails = matchService.getMatchDetails(targetDate);
            log.info("match details: {}", matchDetails);

            List<MatchTableRow> crawledMatches = crawlerService.startCrawling(targetDate);
            log.info("crawledMatches: {}", crawledMatches);

            matchService.updateMatchesFromCrawledData(crawledMatches, matchDetails);
            // 업데이트 후 다시 조회
            matchDetails = matchService.getMatchDetails(targetDate);

            // TODO 경기 시작 시간이 되면 API 호출 및 ECS TASK 시작
            // TODO 모든 경기가 종료 되었으면 ECS TASK 종료 및 API 호출
        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류가 발생했습니다: {}", e.getMessage(), e);
        }
    }

    private LocalDate parseDate(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Date string is null");
        }

        try {
            LocalDate date = LocalDate.parse(str, ISO_DATE_FORMATTER);
            return date.atStartOfDay(KST_ZONE).toLocalDate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Date string is invalid");
        }
    }
}
