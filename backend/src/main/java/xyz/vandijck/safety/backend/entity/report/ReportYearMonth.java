package xyz.vandijck.safety.backend.entity.report;

import javax.validation.constraints.NotNull;


public record ReportYearMonth(@NotNull int year, @NotNull int month) {}
