package xyz.vandijck.safety.entity.observation;

import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObservationPatchInput {

	@NotNull
	private Status status;

	@JsonCreator
	ObservationPatchInput(
			Status status
	) {
		this.status = status;
	}

}
