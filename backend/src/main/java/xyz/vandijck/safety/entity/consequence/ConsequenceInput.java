package xyz.vandijck.safety.entity.consequence;

import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ConsequenceInput {

	@NotBlank
	private final String name;

	@NotNull
	private final Double probability;

	@JsonCreator
    ConsequenceInput(String name, Double probability) {
		this.name = name;
		this.probability = probability;
	}

}
