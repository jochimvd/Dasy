package xyz.vandijck.safety.service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import xyz.vandijck.safety.entity.analysis.AnalysisYearMonth;
import xyz.vandijck.safety.entity.analysis.CategoryAnalysis;
import xyz.vandijck.safety.entity.category.Category;

@Log4j2
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final CategoryService categoryService;

    private final ObservationService observationService;

    public List<CategoryAnalysis> calculateRiskScore(AnalysisYearMonth analysisYearMonth) {
        YearMonth observedMonth = YearMonth.of(
                analysisYearMonth.year(), analysisYearMonth.month());

        ZonedDateTime firstDayOfMonth = observedMonth.atDay(1)
                .atStartOfDay(ZoneId.systemDefault());

        ZonedDateTime lastDayOfMonth = observedMonth.atEndOfMonth()
                .atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        return calculateRiskScores(firstDayOfMonth, lastDayOfMonth);
    }

    public List<CategoryAnalysis> calculateRiskScores(ZonedDateTime start, ZonedDateTime end) {
        List<CategoryAnalysis> riskScores = new ArrayList<>();
        for (Category category : categoryService.findAll()) {
            riskScores.add(new CategoryAnalysis(
                    category, calculateRiskScore(category, start, end)));
        }

        return riskScores;
    }

    private double calculateRiskScore(Category category, ZonedDateTime start, ZonedDateTime end) {

        double riskScore =
                category.getConsequence().getProbability()
                        * category.getSeverity().getLevel()
                        * calculateKinneyScore(category, start, end, true);

        log.info("R = " + riskScore);

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

        log.info("Category: " + category.getName());

        log.info("Start date: " + start);
        log.info("End date:   " + end);

        log.info("Observations: " + observations);
        log.info("daysObserved: " + daysObserved);

        log.info("L = " + intervalWeight);
        log.info("N = " + interval);
        log.info("O = " + occurrencesPerYearFromInterval);
        log.info("G = " + intervalLowerBound);
        log.info("B = " + derivedKinneyScore);

        return derivedKinneyScore;
    }

    private double avgOccurrencesPerYear(Category category, ZonedDateTime end) {
        final ZonedDateTime startOfMeasurements = ZonedDateTime.parse("2022-01-01T00:00+01:00"); // TODO: make this a setting
        int daysInYear = Year.of(startOfMeasurements.getYear()).length();
        long daysObserved = ChronoUnit.DAYS.between(startOfMeasurements, end) + 1;
        long observations = observationService.countByObservedAtBeforeAndCategoryId(
                end, category.getId());

        return (double) observations / daysObserved * daysInYear;
    }
}
