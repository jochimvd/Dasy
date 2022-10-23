package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.entity.common.Language;
import xyz.vandijck.safety.backend.service.SearchBuilder;

import java.util.List;
import java.util.Map;

/**
 * User search request.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchRequest extends EmailUserSearchRequest {

    // Data field filters
    private String firstName;
    private String lastName;

    private String zipCode;
    private String city;
    private String country;
    private List<Language> languages;

    // Due to the fact that some fields are nested, we need to translate them from request to internal name.
    private static final Map<String, String> mapper = Map.of(
            "country",          "address.country",
            "zipCode",          "address.zipCode",
            "streetWithNumber", "address.streetWithNumber",
            "city",             "address.city"
    );

    @Override
    public String translateField(String field) {
        return mapper.getOrDefault(field, field);
    }

    @Override
    public void applyFilters(SearchBuilder sb) {
        super.applyFilters(sb);
        sb.addFuzzyFilter(
                firstName,
                "firstName");
        sb.addFuzzyFilter(
                lastName,
                "lastName");
        sb.addFilter(
                languages,
                "languages");
        sb.addFilter(
                country,
                "address.country");
        sb.addFilterStar(
                zipCode,
                "address.zipCode");
        sb.addFuzzyFilter(
                city,
                "address.city");
    }

    @Override
    public Link createLinkInternal(LinkRelation linkRel, UriComponentsBuilder builder) {
        super.createLinkInternal(linkRel, builder);
        addToBuilder(builder, "firstName", firstName);
        addToBuilder(builder, "lastName", lastName);
        addToBuilder(builder, "languages", languages);
        addToBuilder(builder, "country", country);
        addToBuilder(builder, "zipCode", zipCode);
        addToBuilder(builder, "city", city);
        return Link.of(builder.toUriString(), linkRel);
    }
}
