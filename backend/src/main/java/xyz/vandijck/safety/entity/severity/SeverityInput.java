package xyz.vandijck.safety.entity.severity;

import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SeverityInput {

	@NotBlank
	private final String name;

	@NotNull
	private final double level;

	@JsonCreator
	SeverityInput(String name, double level) {
		this.name = name;
		this.level = level;
	}

}
