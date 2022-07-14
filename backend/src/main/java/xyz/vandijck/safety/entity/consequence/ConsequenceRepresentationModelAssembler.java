package xyz.vandijck.safety.entity.consequence;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.ConsequenceController;


@Component
public class ConsequenceRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Consequence, ConsequenceRepresentationModelAssembler.ConsequenceModel> {

	public ConsequenceRepresentationModelAssembler() {
		super(ConsequenceController.class, ConsequenceModel.class);
	}

	@Override
	public ConsequenceModel toModel(Consequence entity) {
		ConsequenceModel consequenceModel = createModelWithId(entity.getId(), entity);
		return consequenceModel;
	}
	
	@Override
	protected ConsequenceModel instantiateModel(Consequence entity) {
		return new ConsequenceModel(entity);
	}


	@Relation(collectionRelation = "consequences", itemRelation = "consequence")
	public static class ConsequenceModel extends RepresentationModel<ConsequenceModel> {
		
		private final Consequence consequence;

		ConsequenceModel(Consequence consequence) {
			this.consequence = consequence;
		}

		public long getId() {
			return consequence.getId();
		}
		
		public String getName() {
			return consequence.getName();
		}

		public Double getProbability() {
			return consequence.getProbability();
		}
		
	}

}
