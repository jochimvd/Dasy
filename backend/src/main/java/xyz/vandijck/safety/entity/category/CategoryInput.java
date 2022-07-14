package xyz.vandijck.safety.entity.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CategoryInput {

	@NotBlank
	private final String name;

	@NotNull
	private final URI consequenceURI;

	@NotNull
	private final URI severityURI;

	@JsonCreator
	CategoryInput(String name, @JsonProperty("consequence") URI consequenceURI, @JsonProperty("severity") URI severityURI) {
		this.name = name;
		this.consequenceURI = consequenceURI;
		this.severityURI = severityURI;
	}

}
