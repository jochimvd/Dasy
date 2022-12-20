package xyz.vandijck.safety.backend.assembler.report;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.controller.SiteController;
import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.entity.report.SiteReport;


@Component
public class SiteReportRepresentationModelAssembler extends RepresentationModelAssemblerSupport<SiteReport, SiteReportRepresentationModelAssembler.SiteAnalysisModel> {

    public SiteReportRepresentationModelAssembler() {
        super(SiteController.class, SiteAnalysisModel.class);
    }

    @Override
    public SiteAnalysisModel toModel(SiteReport entity) {
        SiteAnalysisModel siteAnalysisModel = createModelWithId(entity.getSite().getId(), entity);
        return siteAnalysisModel;
    }

    @Override
    protected SiteAnalysisModel instantiateModel(SiteReport entity) {
        return new SiteAnalysisModel(entity);
    }

    @Relation(collectionRelation = "sites", itemRelation = "site")
    public static class SiteAnalysisModel extends RepresentationModel<SiteAnalysisModel> {

        private final SiteReport siteReport;

        SiteAnalysisModel(SiteReport siteReport) {
            this.siteReport = siteReport;
        }

        public Site getSite() {
            return siteReport.getSite();
        }

        public long getObservations() {
            return siteReport.getObservations();
        }

        public long getPositiveObservations() {
            return siteReport.getPositiveObservations();
        }

    }

}
