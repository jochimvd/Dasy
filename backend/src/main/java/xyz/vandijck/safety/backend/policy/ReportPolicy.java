package xyz.vandijck.safety.backend.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.entity.report.ReportStartEnd;
import xyz.vandijck.safety.backend.entity.report.ReportYearMonth;
import xyz.vandijck.safety.backend.service.ReportService;

import java.time.ZonedDateTime;

@Component
public class ReportPolicy {

    @Value("${app.report.start-of-observations}")
    private String startOfObservations;

    @Autowired
    private ReportService reportService;

    public void authorizeGet(ReportStartEnd reportStartEnd) {
        if (ZonedDateTime.parse(startOfObservations).isAfter(reportStartEnd.endDate())) {
//            throw new BadRequestException("endDate", "endDate invalid");
        }
    }

    public void authorizeGet(ReportYearMonth reportYearMonth) {
        authorizeGet(reportService.calculateStartEnd(reportYearMonth));
    }

}
