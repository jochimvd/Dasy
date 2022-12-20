package xyz.vandijck.safety.backend.entity.report;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;


public record ReportStartEnd(@NotNull ZonedDateTime startDate, @NotNull ZonedDateTime endDate) {}
