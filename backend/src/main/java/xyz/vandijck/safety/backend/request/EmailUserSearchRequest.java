package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.service.SearchBuilder;

/**
 * User search request.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class EmailUserSearchRequest extends SearchRequest {
    private String email;

    @Override
    public void applyFilters(SearchBuilder sb) {
        sb.addFuzzyFilter(email, "email");
    }

    @Override
    public Link createLinkInternal(LinkRelation linkRel, UriComponentsBuilder builder) {
        addToBuilder(builder, "email", email);
        return Link.of(builder.toUriString(), linkRel);
    }
}
