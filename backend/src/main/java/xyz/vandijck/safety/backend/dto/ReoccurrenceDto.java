package xyz.vandijck.safety.backend.dto;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.hateoas.server.core.Relation;
import xyz.vandijck.safety.backend.assembler.Views;
import xyz.vandijck.safety.backend.entity.UniqueEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * A Data Transfer Object exposing only the needed fields to the JSON representation
 */
@Data
@Relation(collectionRelation = "reoccurrences")
@Accessors(chain = true)
public class ReoccurrenceDto implements UniqueEntity {

    @JsonView(Views.Reader.class)
    private long id;

    @JsonView(Views.Reader.class)
    @NotBlank(message = "isEmpty")
    private String name;

    @JsonView(Views.Reader.class)
    private String description;

    @JsonView(Views.Reader.class)
    @NotNull(message = "isEmpty")
    @Min(0)
    private Double rate;

}
