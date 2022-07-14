package xyz.vandijck.safety.entity.analysis;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;


public record AnalysisStartEnd(@NotNull ZonedDateTime startDate, @NotNull ZonedDateTime endDate) {}
