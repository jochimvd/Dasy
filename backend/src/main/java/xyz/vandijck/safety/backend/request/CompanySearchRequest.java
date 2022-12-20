package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.service.SearchBuilder;

/**
 * Company search request.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CompanySearchRequest extends SearchRequest {

    // Data field filters
    private String name;

    @Override
    public boolean shouldAddArchiveFilter() {
        return false;
    }

    @Override
    public void applyFilters(SearchBuilder sb) {
        sb.addFuzzyFilter(name, "name");
    }

    @Override
    public Link createLinkInternal(LinkRelation linkRel, UriComponentsBuilder builder) {
        addToBuilder(builder, "name", name);
        return Link.of(builder.toUriString(), linkRel);
    }
}
