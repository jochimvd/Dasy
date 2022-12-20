package xyz.vandijck.safety.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.vandijck.safety.backend.entity.Category;
import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.entity.report.CategoryReport;
import xyz.vandijck.safety.backend.entity.report.ReportStartEnd;
import xyz.vandijck.safety.backend.entity.report.ReportYearMonth;
import xyz.vandijck.safety.backend.entity.report.SiteReport;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    // TODO: make this a setting
    @Value("${app.report.start-of-observations}")
    private String startOfObservations;

    private final CategoryService categoryService;

    private final SiteService siteService;

    private final ObservationService observationService;


    public ReportStartEnd calculateStartEnd(ReportYearMonth reportYearMonth) {
        YearMonth observedMonth = YearMonth.of(
                reportYearMonth.year(), reportYearMonth.month());

        ZonedDateTime firstDayOfMonth = observedMonth.atDay(1)
                .atStartOfDay(ZoneId.systemDefault());

        ZonedDateTime lastDayOfMonth = observedMonth.atEndOfMonth()
                .atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        return new ReportStartEnd(firstDayOfMonth, lastDayOfMonth);
    }

    public List<SiteReport> getSiteReports(ReportYearMonth reportYearMonth) {
        return getSiteReports(calculateStartEnd(reportYearMonth));
    }

    public List<SiteReport> getSiteReports(ReportStartEnd reportStartEnd) {
        List<SiteReport> siteReports = new ArrayList<>();
        for (Site site : siteService.findAll()) {

            long positiveObservations = observationService.countByObservedAtBetweenAndSiteIdAndTypeId(
                    reportStartEnd.startDate(), reportStartEnd.endDate(), site.getId(), 1
            );
            long observations = observationService.countByObservedAtBetweenAndSiteId(
                    reportStartEnd.startDate(), reportStartEnd.endDate(), site.getId()
            );

            siteReports.add(new SiteReport(
                    site,
                    observations,
                    positiveObservations
            ));
        }

        return siteReports;
    }

    public List<CategoryReport> calculateRiskScore(ReportYearMonth reportYearMonth) {
        return calculateRiskScores(calculateStartEnd(reportYearMonth));
    }

    public List<CategoryReport> calculateRiskScores(ReportStartEnd reportStartEnd) {
        List<CategoryReport> riskScores = new ArrayList<>();
        for (Category category : categoryService.findAll()) {
            riskScores.add(new CategoryReport(
                    category,
                    calculateRiskScore(category, ZonedDateTime.parse(startOfObservations), reportStartEnd.endDate())));
        }

        return riskScores;
    }

    private double calculateRiskScore(Category category, ZonedDateTime start, ZonedDateTime end) {

        double riskScore =
                category.getReoccurrence().getRate()
                        * category.getSeverityLevel()
                        * calculateKinneyScore(category, start, end, true);

        log.debug("R = " + riskScore);

        return riskScore;
    }

    private double calculateKinneyScore(Category category, ZonedDateTime start, ZonedDateTime end, boolean allObservations) {
        int[] occurrenceIntervals = new int[] {0, 1, 4, 12, 48, 120, Integer.MAX_VALUE};
        double[] occurrenceWeights = new double[] {0., .5, 1., 2., 3., 6., 10.};

        int daysInYear = Year.of(start.getYear()).length();
        long daysObserved = ChronoUnit.DAYS.between(start, end) + 1;
        long observations = observationService.countByObservedAtBetweenAndCategoryId(
                start, end, category.getId());
        double occurrencesPerYearFromInterval = (double) observations / daysObserved * daysInYear;

        int interval = 0;
        int intervalLowerBound = 0;
        double intervalWeight = 0.;
        double derivedKinneyScore = 0.;

        if (occurrencesPerYearFromInterval > 0) {
            for (int i = 0; i < occurrenceIntervals.length; i++) {
                if (occurrencesPerYearFromInterval < occurrenceIntervals[i]) {
                    interval = occurrenceIntervals[i] - occurrenceIntervals[i - 1];
                    intervalLowerBound = occurrenceIntervals[i - 1];
                    intervalWeight = occurrenceWeights[i - 1];

                    double occurrences = allObservations ?
                            avgOccurrencesPerYear(category, end) : occurrencesPerYearFromInterval;

                    derivedKinneyScore =
                            intervalWeight + (intervalWeight / interval) * (occurrences - intervalLowerBound);

                    break;
                }
            }
        }

        log.debug("Category: " + category.getName());

        log.debug("Start date: " + start);
        log.debug("End date:   " + end);

        log.debug("Observations: " + observations);
        log.debug("daysObserved: " + daysObserved);

        log.debug("L = " + intervalWeight);
        log.debug("N = " + interval);
        log.debug("O = " + occurrencesPerYearFromInterval);
        log.debug("G = " + intervalLowerBound);
        log.debug("B = " + derivedKinneyScore);

        return derivedKinneyScore;
    }

    private double avgOccurrencesPerYear(Category category, ZonedDateTime end) {
        final ZonedDateTime startOfMeasurements = ZonedDateTime.parse(startOfObservations);
        int daysInYear = Year.of(startOfMeasurements.getYear()).length();
        long daysObserved = ChronoUnit.DAYS.between(startOfMeasurements, end) + 1;
        long observations = observationService.countByObservedAtBeforeAndCategoryId(
                end, category.getId());

        return (double) observations / daysObserved * daysInYear;
    }
}
