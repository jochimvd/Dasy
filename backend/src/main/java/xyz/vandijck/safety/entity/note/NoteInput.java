package xyz.vandijck.safety.entity.note;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotBlank;

public class NoteInput {

	@NotBlank
	private final String title;

	private final String body;

	private final List<URI> tagUris;

	@JsonCreator
	NoteInput(@JsonProperty("title") String title,
			@JsonProperty("body") String body, @JsonProperty("tags") List<URI> tagUris) {
		this.title = title;
		this.body = body;
		this.tagUris = tagUris == null ? Collections.emptyList() : tagUris;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	@JsonProperty("tags")
	public List<URI> getTagUris() {
		return this.tagUris;
	}

}
