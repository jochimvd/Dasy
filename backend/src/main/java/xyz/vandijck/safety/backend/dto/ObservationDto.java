package xyz.vandijck.safety.backend.dto;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.hateoas.server.core.Relation;
import xyz.vandijck.safety.backend.assembler.Views;
import xyz.vandijck.safety.backend.entity.UniqueEntity;
import xyz.vandijck.safety.backend.entity.common.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * A Data Transfer Object exposing only the needed fields to the JSON representation
 */
@Data
@Relation(collectionRelation = "observations")
@Accessors(chain = true)
public class ObservationDto implements UniqueEntity {

    @JsonView(Views.Reader.class)
    private long id;

    @JsonView(Views.Reader.class)
    @NotNull(message = "isNull")
    private UserDto observer;

    @JsonView(Views.Reader.class)
    @NotNull(message = "isNull")
    private ZonedDateTime observedAt;

    @JsonView(Views.Reader.class)
    @NotBlank(message = "isBlank")
    private String observedCompany;

    @JsonView(Views.Reader.class)
    @NotNull(message = "isNull")
    private Boolean immediateDanger;

    @JsonView(Views.Reader.class)
    @NotNull(message = "isNull")
    private LocationDto location;

    @JsonView(Views.Reader.class)
    @NotNull(message = "isNull")
    private CategoryDto category;

    @JsonView(Views.Reader.class)
    private Status status;

    @JsonView(Views.Reader.class)
    private String type;

    @JsonView(Views.Reader.class)
    private String description;

    @JsonView(Views.Reader.class)
    private String actionsTaken;

    @JsonView(Views.Reader.class)
    private String furtherActions;


    public String getKey() {
        return "SOR-" + location.getName().substring(0,3).toUpperCase() + "-" + id;
    }
}
