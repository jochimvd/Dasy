package xyz.vandijck.safety.backend.request;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.vandijck.safety.backend.assembler.Views;
import xyz.vandijck.safety.backend.dto.AddressDto;
import xyz.vandijck.safety.backend.entity.common.Language;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * Request body of a PUT request to /users/{id}
 * Cannot be a DTO object as not all fields are allowed to be edited at all times
 */
@Data
@Accessors(chain = true)
public class UpdateUserRequest {

    // Fields that are present in a child and a full user
    private long id;

    @NotBlank(message = "isEmpty")
    private String firstName;

    @NotBlank(message = "isEmpty")
    private String lastName;

    @JsonView({Views.Editor.class, Views.Personal.class})
    private String specialInfo;

    @NotNull(message = "isEmpty")
    private LocalDate dateOfBirth;


    // Fields that are present in full users only, inherited by children and thus not editable in them

    private String phoneNumber;

    private Boolean picturePermission;

    private AddressDto address;

    private Set<Language> languages;
}
