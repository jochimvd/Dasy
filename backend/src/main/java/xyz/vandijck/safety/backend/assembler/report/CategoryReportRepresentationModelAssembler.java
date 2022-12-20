package xyz.vandijck.safety.backend.assembler.report;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.controller.CategoryController;
import xyz.vandijck.safety.backend.entity.Category;
import xyz.vandijck.safety.backend.entity.report.CategoryReport;


@Component
public class CategoryReportRepresentationModelAssembler extends RepresentationModelAssemblerSupport<CategoryReport, CategoryReportRepresentationModelAssembler.CategoryAnalysisModel> {

    public CategoryReportRepresentationModelAssembler() {
        super(CategoryController.class, CategoryAnalysisModel.class);
    }

    @Override
    public CategoryAnalysisModel toModel(CategoryReport entity) {
        CategoryAnalysisModel categoryAnalysisModel = createModelWithId(entity.getCategory().getId(), entity);
        return categoryAnalysisModel;
    }

    @Override
    protected CategoryAnalysisModel instantiateModel(CategoryReport entity) {
        return new CategoryAnalysisModel(entity);
    }

    @Relation(collectionRelation = "categories", itemRelation = "category")
    public static class CategoryAnalysisModel extends RepresentationModel<CategoryAnalysisModel> {

        private final CategoryReport categoryReport;

        CategoryAnalysisModel(CategoryReport categoryReport) {
            this.categoryReport = categoryReport;
        }

        public Category getCategory() {
            return categoryReport.getCategory();
        }

        public double getScore() {
            return categoryReport.getScore();
        }

    }

}
