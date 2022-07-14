package xyz.vandijck.safety.entity.severity;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.SeverityController;

@Component
public class SeverityRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Severity, SeverityRepresentationModelAssembler.SeverityModel> {

	public SeverityRepresentationModelAssembler() {
		super(SeverityController.class, SeverityModel.class);
	}

	@Override
	public SeverityModel toModel(Severity entity) {
		SeverityModel severityModel = createModelWithId(entity.getId(), entity);
		return severityModel;
	}
	
	@Override
	protected SeverityModel instantiateModel(Severity entity) {
		return new SeverityModel(entity);
	}

	@Relation(collectionRelation = "severities", itemRelation = "severity")
	public static class SeverityModel extends RepresentationModel<SeverityModel> {
		
		private final Severity severity;

		SeverityModel(Severity severity) {
			this.severity = severity;
		}

		public long getId() {
			return severity.getId();
		}
		
		public String getName() {
			return severity.getName();
		}

		public Double getLevel() {
			return severity.getLevel();
		}
		
	}

}
