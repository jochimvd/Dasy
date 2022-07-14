package xyz.vandijck.safety.entity.analysis;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.CategoryController;


@Component
public class CategoryAnalysisRepresentationModelAssembler extends RepresentationModelAssemblerSupport<CategoryAnalysis, CategoryAnalysisRepresentationModelAssembler.CategoryAnalysisModel> {

	public CategoryAnalysisRepresentationModelAssembler() {
		super(CategoryController.class, CategoryAnalysisModel.class);
	}

	@Override
	public CategoryAnalysisModel toModel(CategoryAnalysis entity) {
		CategoryAnalysisModel categoryAnalysisModel = createModelWithId(entity.getCategory().getId(), entity);
		return categoryAnalysisModel;
	}

	@Override
	protected CategoryAnalysisModel instantiateModel(CategoryAnalysis entity) {
		return new CategoryAnalysisModel(entity);
	}

	@Relation(collectionRelation = "categories", itemRelation = "category")
	public static class CategoryAnalysisModel extends RepresentationModel<CategoryAnalysisModel> {

		private final CategoryAnalysis categoryAnalysis;

		CategoryAnalysisModel(CategoryAnalysis categoryAnalysis) {
			this.categoryAnalysis = categoryAnalysis;
		}

		public String getName() {
			return categoryAnalysis.getCategory().getName();
		}

		public double getPriority() {
			return categoryAnalysis.getPriority();
		}

	}

}
