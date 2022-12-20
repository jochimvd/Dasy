package xyz.vandijck.safety.backend.controller;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.vandijck.safety.backend.assembler.report.CategoryReportRepresentationModelAssembler;
import xyz.vandijck.safety.backend.assembler.report.SiteReportRepresentationModelAssembler;
import xyz.vandijck.safety.backend.entity.report.ReportStartEnd;
import xyz.vandijck.safety.backend.entity.report.ReportYearMonth;
import xyz.vandijck.safety.backend.policy.ReportPolicy;
import xyz.vandijck.safety.backend.service.ReportService;

import javax.validation.Valid;


@AllArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private final ReportService reportService;

    @Autowired
    private final CategoryReportRepresentationModelAssembler categoryReportRepresentationModelAssembler;

    @Autowired
    private final SiteReportRepresentationModelAssembler siteReportRepresentationModelAssembler;

    @Autowired
    private final ReportPolicy reportPolicy;


    @RequestMapping(value = "/categories/period", method = RequestMethod.POST)
    public CollectionModel<CategoryReportRepresentationModelAssembler.CategoryAnalysisModel>
    getCategoryReport(@Valid @RequestBody ReportStartEnd reportStartEnd) {
        reportPolicy.authorizeGet(reportStartEnd);
        return categoryReportRepresentationModelAssembler.toCollectionModel(
                reportService.calculateRiskScores(reportStartEnd));
    }

    @RequestMapping(value = "/categories/month", method = RequestMethod.POST)
    public CollectionModel<CategoryReportRepresentationModelAssembler.CategoryAnalysisModel>
    getCategoryReport(@Valid @RequestBody ReportYearMonth reportYearMonth) {
        reportPolicy.authorizeGet(reportYearMonth);
        return categoryReportRepresentationModelAssembler.toCollectionModel(
                reportService.calculateRiskScore(reportYearMonth));
    }

    @RequestMapping(value = "/sites/period", method = RequestMethod.POST)
    public CollectionModel<SiteReportRepresentationModelAssembler.SiteAnalysisModel>
    getSiteReport(@Valid @RequestBody ReportStartEnd reportStartEnd) {
        reportPolicy.authorizeGet(reportStartEnd);
        return siteReportRepresentationModelAssembler.toCollectionModel(
                reportService.getSiteReports(reportStartEnd));
    }

    @RequestMapping(value = "/sites/month", method = RequestMethod.POST)
    public CollectionModel<SiteReportRepresentationModelAssembler.SiteAnalysisModel>
    getSiteReport(@Valid @RequestBody ReportYearMonth reportYearMonth) {
        reportPolicy.authorizeGet(reportYearMonth);
        return siteReportRepresentationModelAssembler.toCollectionModel(
                reportService.getSiteReports(reportYearMonth));
    }
}
