package xyz.vandijck.safety.entity.observation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.time.ZonedDateTime;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.ObservationController;
import xyz.vandijck.safety.entity.category.CategoryRepresentationModelAssembler;
import xyz.vandijck.safety.entity.location.LocationRepresentationModelAssembler;

@Component
public class ObservationRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Observation, ObservationRepresentationModelAssembler.ObservationModel> {

	ObservationRepresentationModelAssembler() {
		super(ObservationController.class, ObservationModel.class);
	}

	@Override
	public ObservationModel toModel(Observation entity) {
		ObservationModel observationModel = new ObservationModel(entity);

		observationModel.add(linkTo(methodOn(ObservationController.class).get(entity.getId()))
				.withSelfRel()
				.andAffordance(afford(methodOn(ObservationController.class).update(entity.getId(), null))).withTitle("Update")
				.andAffordance(afford(methodOn(ObservationController.class).delete(entity.getId()))).withTitle("Delete"));

		return observationModel;
	}

	@Override
	protected ObservationModel instantiateModel(Observation entity) {
		return new ObservationModel(entity);
	}

	@Relation(collectionRelation = "observations", itemRelation = "observation")
	public static class ObservationModel extends RepresentationModel<ObservationModel> {
		
		private final Observation observation;

		ObservationModel(Observation observation) {
			this.observation = observation;
		}

		public long getId() {
			return observation.getId();
		}
		
		public String getKey() {
			return observation.getKey();
		}

		public String getObserver() {
			return observation.getObserver();
		}

		public String getObserverCompany() {
			return observation.getObserverCompany();
		}

		public ZonedDateTime getObservedAt() {
			return observation.getObservedAt();
		}

		public int getWeekNr() {
			return observation.getWeekNr();
		}

		public LocationRepresentationModelAssembler.LocationModel getLocation() {
			return new LocationRepresentationModelAssembler().toModel(observation.getLocation());
		}

		public String getObservedCompany() {
			return observation.getObservedCompany();
		}

		public boolean isImmediateDanger() {
			return observation.isImmediateDanger();
		}

		public String getType() {
			return observation.getType();
		}

		public CategoryRepresentationModelAssembler.CategoryModel getCategory() {
			return new CategoryRepresentationModelAssembler().toModel(observation.getCategory());
		}

		public String getDescription() {
			return observation.getDescription();
		}

		public String getActionsTaken() {
			return observation.getActionsTaken();
		}

		public String getFurtherActions() {
			return observation.getFurtherActions();
		}

		public Status getStatus() {
			return observation.getStatus();
		}

		public ZonedDateTime getCreatedAt() {
			return observation.getCreatedAt();
		}

		public ZonedDateTime getUpdatedAt() {
			return observation.getUpdatedAt();
		}
		
	}

}
