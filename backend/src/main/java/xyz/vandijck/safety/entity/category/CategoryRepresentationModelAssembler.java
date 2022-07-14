package xyz.vandijck.safety.entity.category;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.CategoryController;
import xyz.vandijck.safety.entity.consequence.ConsequenceRepresentationModelAssembler;
import xyz.vandijck.safety.entity.severity.SeverityRepresentationModelAssembler;


@Component
public class CategoryRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Category, CategoryRepresentationModelAssembler.CategoryModel> {

	public CategoryRepresentationModelAssembler() {
		super(CategoryController.class, CategoryModel.class);
	}

	@Override
	public CategoryModel toModel(Category entity) {
		CategoryModel categoryModel = createModelWithId(entity.getId(), entity);
		return categoryModel;
	}

	@Override
	protected CategoryModel instantiateModel(Category entity) {
		return new CategoryModel(entity);
	}


	@Relation(collectionRelation = "categories", itemRelation = "category")
	public static class CategoryModel extends RepresentationModel<CategoryModel> {
		
		private final Category category;

		CategoryModel(Category category) {
			this.category = category;
		}


		public long getId() {
			return category.getId();
		}
		
		public String getName() {
			return category.getName();
		}

		public SeverityRepresentationModelAssembler.SeverityModel getSeverity() {
			return new SeverityRepresentationModelAssembler().toModel(category.getSeverity());
		}

		public ConsequenceRepresentationModelAssembler.ConsequenceModel getConsequence() {
			return new ConsequenceRepresentationModelAssembler().toModel(category.getConsequence());
		}
		
	}

}
