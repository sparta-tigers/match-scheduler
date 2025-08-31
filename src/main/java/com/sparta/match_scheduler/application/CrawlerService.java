package com.sparta.match_scheduler.application;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.sparta.match_scheduler.domain.MatchTableRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrawlerService {

    private static final String TARGET_URL = "https://www.koreabaseball.com/Schedule/Schedule.aspx";

    public List<MatchTableRow> startCrawling(LocalDate targetDate) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch()
        ) {
            int year = targetDate.getYear();
            int month = targetDate.getMonthValue();
            log.info("크롤링 시작");
            Page page = browser.newPage();
            page.navigate(TARGET_URL);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            log.info("페이지 로드 완료");
            // 년도, 월 선택
            selectYear(page, year);
            selectMonth(page, month);

            return extractScheduleRows(page, year, month).stream()
                    .filter(match -> match.matchTime().toLocalDate().equals(targetDate))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("크롤링 도중 문제가 발생 했습니다. {}", e.getMessage(), e);
            throw new RuntimeException("크롤링 도중 문제가 발생 했습니다.");
        }
    }

    private void selectYear(Page page, int year) {
        page.waitForSelector("#ddlYear");
        page.selectOption("#ddlYear", String.valueOf(year));
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * 월을 2자리(01, 02, ...) 형식으로 선택하는 함수
     * @param page Playwright 페이지 객체
     * @param month 선택할 월(정수, 1~12)
     */
    private void selectMonth(Page page, int month) {
        String formattedMonth = String.format("%02d", month);
        page.waitForSelector("#ddlMonth");
        page.selectOption("#ddlMonth", formattedMonth);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * 스케줄 테이블의 각 tr을 순회하여 필요한 경기 정보를 추출한다.
     * - 날짜(td.day)는 첫 행에만 존재(rowspan)하므로 값 누적이 필요하다.
     * - 첫 행은 9열(날짜 포함), 이후 행은 8열이므로 구장/비고 인덱스를 보정한다.
     * - 하이라이트 a 태그 존재 여부로 경기 진행 여부를 추정한다.
     *
     * @param page Playwright 페이지
     * @return 추출된 경기 행 목록
     */
    private List<MatchTableRow> extractScheduleRows(Page page, int year, int month) {
        List<MatchTableRow> resultList = new ArrayList<>();
        Locator rows = page.locator("table#tblScheduleList > tbody > tr");
        int rowCount = rows.count();

        if (rowCount == 1) {
            log.info("경기 데이터가 없습니다.");
            return resultList;
        }

        int currentDay = 0;

        for (int i = 0; i < rowCount; i++) {
            Locator row = rows.nth(i);
            List<String> cellTexts = row.locator("td").allInnerTexts();
            if (cellTexts.isEmpty()) {
                continue;
            }

            boolean hasDayCell = row.locator("td.day").count() > 0;
            if (hasDayCell) {
                // 예: "08.01(금)" → "08.01" 로 정규화
                currentDay = Integer.parseInt(normalizeKboDateLabel(row.locator("td.day").innerText().trim()).split("\\.")[1]);
            }

            String gameTime = row.locator("td.time b").innerText().trim();
            Locator playCell = row.locator("td.play");
            String awayTeamName = playCell.locator(":scope > span").first().innerText().trim();
            String homeTeamName = playCell.locator(":scope > span").last().innerText().trim();
            String scoreText = playCell.locator("em").innerText().trim();
            int awayTeamScore = 0;
            int homeTeamScore = 0;
            int[] parsedScores = parseTwoScores(scoreText);
            awayTeamScore = parsedScores[0];
            homeTeamScore = parsedScores[1];

            boolean hasHighlight = row.locator("a#btnHighlight, a:has-text('하이라이트')").count() > 0;
            int stadiumIndex = hasDayCell ? 7 : 6;
            int remarkIndex = hasDayCell ? 8 : 7;
            String stadiumName = row.locator("td").nth(stadiumIndex).innerText().trim();
            String remark = row.locator("td").nth(remarkIndex).innerText().trim();

            int matchHour = Integer.parseInt(gameTime.split(":")[0]);
            int matchMinute = Integer.parseInt(gameTime.split(":")[1]);
            LocalDateTime matchTime = LocalDateTime.of(year, month, currentDay, matchHour, matchMinute);

            resultList.add(new MatchTableRow(
                    matchTime,
                    awayTeamName,
                    homeTeamName,
                    awayTeamScore,
                    homeTeamScore,
                    stadiumName,
                    remark,
                    hasHighlight
            ));
        }

        return resultList;
    }

    /**
     * KBO 날짜 라벨을 정규화하여 "MM.DD" 형태만 남긴다.
     * 예: "08.01(금)" → "08.01"
     */
    private String normalizeKboDateLabel(String raw) {
        Matcher m = Pattern.compile("^\\s*(\\d{2}\\.\\d{2})").matcher(raw);
        if (m.find()) {
            return m.group(1);
        }

        throw new RuntimeException("KBO 날짜 파싱 오류: " + raw);
    }

    /**
     * 점수 텍스트에서 숫자 2개를 찾아 [away, home] 순으로 반환.
     *
     * @param text 점수 텍스트 ex [7vs2]
     * @return 점수 배열
     */
    private int[] parseTwoScores(String text) {
        String[] parts = text.toLowerCase().split("vs");
        if (parts.length != 2) {
            return new int[]{0, 0};
        }

        String left = parts[0].trim();
        String right = parts[1].trim();

        return new int[]{Integer.parseInt(left), Integer.parseInt(right)};
    }
}
