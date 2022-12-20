package xyz.vandijck.safety.backend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.hateoas.server.core.Relation;
import xyz.vandijck.safety.backend.assembler.Views;
import xyz.vandijck.safety.backend.entity.UniqueEntity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@Relation(collectionRelation = "users")
@Accessors(chain = true)
public class UserDto implements UniqueEntity {

    @JsonView({Views.Reader.class, Views.Personal.class})
    private long id;

    @JsonView({Views.Admin.class, Views.Personal.class})
    private LocalDate registrationDate;

    @JsonView({Views.Reader.class, Views.Personal.class})
    @NotBlank(message = "isEmpty")
    private String firstName;

    @JsonView({Views.Reader.class, Views.Personal.class})
    @NotBlank(message = "isEmpty")
    private String lastName;

    @JsonView({Views.Editor.class, Views.Personal.class})
    @NotBlank(message = "isEmpty")
    @Email(message = "emailInvalid")
    private String email;

    @JsonView({Views.Editor.class, Views.Personal.class})
    @NotBlank(message = "isEmpty")
    private String company;

    @JsonView({Views.Admin.class, Views.Personal.class})
//    @NotNull(message = "isEmpty")
    private AddressDto address;

    @JsonView({Views.Editor.class, Views.Personal.class})
//    @NotEmpty(message = "isEmpty")
    private Set<String> languages;

    @JsonView({Views.Editor.class, Views.Personal.class})
//    @NotBlank(message = "isEmpty")
    private String phoneNumber;

    @JsonView({Views.Editor.class, Views.Personal.class})
    private Boolean activated;


    public UserDto setEmail(String email) {
        if (email != null) {
            this.email = email.toLowerCase();
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return id == userDto.id &&
                Objects.equals(firstName, userDto.firstName) &&
                Objects.equals(lastName, userDto.lastName) &&
                Objects.equals(email, userDto.email) &&
                Objects.equals(languages, userDto.languages) &&
                Objects.equals(phoneNumber, userDto.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, languages, phoneNumber);
    }
}