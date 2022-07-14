package xyz.vandijck.safety.entity.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LocationInput {

	@NotBlank
	private final String name;

	@JsonCreator
    LocationInput(@JsonProperty("name") String name) {
		this.name = name;
	}

}
