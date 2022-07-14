package xyz.vandijck.safety.entity.tag;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TagInput {

	@NotBlank
	private final String name;

	@JsonCreator
	TagInput(@JsonProperty("name") String name) {
		this.name = name;
	}

}
