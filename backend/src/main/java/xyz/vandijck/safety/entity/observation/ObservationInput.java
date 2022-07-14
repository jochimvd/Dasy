package xyz.vandijck.safety.entity.observation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObservationInput {

	@NotBlank
	private String key;

	private String observer;

	private String observerCompany;

	private ZonedDateTime observedAt;

	@NotNull
	private URI locationURI;

	private String observedCompany;

	private boolean immediateDanger;

	private String type;

	@NotNull
	private URI categoryURI;

	private String description;

	private String actionsTaken;

	private String furtherActions;

	@JsonCreator
    ObservationInput(
			String key,
			String observer,
			String observerCompany,
			ZonedDateTime observedAt,
			@JsonProperty("location") URI locationURI,
			String observedCompany,
			boolean immediateDanger,
			String type,
			@JsonProperty("category") URI categoryURI,
			String description,
			String actionsTaken,
			String furtherActions
	) {
		this.key = key;
		this.observer = observer;
		this.observerCompany = observerCompany;
		this.observedAt = observedAt;
		this.locationURI = locationURI;
		this.observedCompany = observedCompany;
		this.immediateDanger = immediateDanger;
		this.type = type;
		this.categoryURI = categoryURI;
		this.description = description;
		this.actionsTaken = actionsTaken;
		this.furtherActions = furtherActions;
	}

}
