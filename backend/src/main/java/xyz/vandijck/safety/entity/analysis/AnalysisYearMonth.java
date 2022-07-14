package xyz.vandijck.safety.entity.analysis;

import javax.validation.constraints.NotNull;


public record AnalysisYearMonth(@NotNull int year, @NotNull int month) {}
