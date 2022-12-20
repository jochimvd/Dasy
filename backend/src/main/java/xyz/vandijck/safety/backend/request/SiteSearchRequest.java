package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.service.SearchBuilder;

/**
 * Site search request.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteSearchRequest extends SearchRequest {

    // Data field filters
    private String name;

    // Spatial search filters
    private Double radius;
    private Double latitude;
    private Double longitude;


    @Override
    public void applyFilters(SearchBuilder sb) {
        sb.addFuzzyFilter(name, "name");
        sb.addSpatialFilter(radius, latitude, longitude, "coordinates");
    }

    @Override
    public Link createLinkInternal(LinkRelation linkRel, UriComponentsBuilder builder) {
        addToBuilder(builder, "name", name);
        addToBuilder(builder, "radius", radius);
        addToBuilder(builder, "latitude", latitude);
        addToBuilder(builder, "longitude", longitude);
        return Link.of(builder.toUriString(), linkRel);
    }
}
