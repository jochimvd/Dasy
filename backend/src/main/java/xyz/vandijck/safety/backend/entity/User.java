package xyz.vandijck.safety.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.vandijck.safety.backend.entity.common.Language;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * A user account with all its details (see domain model).
 */

@Entity
@Indexed
// user is a reserved keyword in some SQL dialects (like postgres), JPA doesn't auto-quote this.
@Table(name = "\"user\"")
@Accessors(chain = true)
@Data
public class User implements UserDetails, UniqueEntity, Archivable {

    private static final long serialVersionUID = -2045186915212940753L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @Field(name = "user_id", store = Store.YES, analyze = Analyze.NO)
    private long id;

    @NotNull
    @Field
    private LocalDate registrationDate;

    @NotBlank
    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Analyzer(definition = "namelike")
    private String firstName;

    @NotBlank
    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Analyzer(definition = "namelike")
    private String lastName;

    @Column(unique = true)
    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Analyzer(definition = "namelike")
    @Email
    private String email;

    @JsonIgnore
    @ToString.Exclude
    @Column(length = 60) // bcrypt property
    private String password;

    @IndexedEmbedded
    @Embedded
    private Address address;

    @ElementCollection(fetch = FetchType.EAGER)
    @Field(analyze = Analyze.NO, store = Store.YES)
    @IndexedEmbedded
    @Column(name = "languages", nullable = false)
    private Set<Language> languages;

    private String phoneNumber;

    private boolean picturePermission;

    private String specialInfo;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Field(analyze = Analyze.NO, store = Store.YES)
    private boolean archived;

    @Column(nullable = false)
    private boolean activated;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return getEmail();
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !isArchived();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !isArchived() && isActivated();
    }

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Analyzer(definition = "namelike")
    public String getFullName() {
        return firstName + " " + lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if(id == user.id){
            return archived == user.archived &&
                    picturePermission == user.picturePermission &&
                    firstName.equals(user.firstName) &&
                    lastName.equals(user.lastName) &&
                    email.equals(user.email) &&
                    address.equals(user.address) &&
                    phoneNumber.equals(user.phoneNumber) &&
                    Objects.equals(specialInfo, user.specialInfo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, address, phoneNumber, picturePermission, specialInfo, archived, activated);
    }
}
