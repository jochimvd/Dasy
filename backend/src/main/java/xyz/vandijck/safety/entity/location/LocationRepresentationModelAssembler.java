package xyz.vandijck.safety.entity.location;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.controller.LocationController;


@Component
public class LocationRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Location, LocationRepresentationModelAssembler.LocationModel> {

	public LocationRepresentationModelAssembler() {
		super(LocationController.class, LocationModel.class);
	}

	@Override
	public LocationModel toModel(Location entity) {
		LocationModel locationModel = createModelWithId(entity.getId(), entity);
		return locationModel;
	}
	
	@Override
	protected LocationModel instantiateModel(Location entity) {
		return new LocationModel(entity);
	}


	@Relation(collectionRelation = "locations", itemRelation = "location")
	public static class LocationModel extends RepresentationModel<LocationModel> {
		
		private final Location location;

		LocationModel(Location location) {
			this.location = location;
		}

		public long getId() {
			return location.getId();
		}
		
		public String getName() {
			return location.getName();
		}
		
	}

}
