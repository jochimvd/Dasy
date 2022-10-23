package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.entity.common.Status;
import xyz.vandijck.safety.backend.service.SearchBuilder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Observation search request.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ObservationSearchRequest extends SearchRequest {

    // Data field filters
    private String search;

    private String observerName;

    private String observedCompany;

    private String categoryName;

    private String locationName;

    // Collection filters
    private List<Long> category;

    private List<Long> location;

    private List<Status> status;

    private List<Boolean> immediateDanger;

    // Temporal search filters
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime before;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime after;

    private static final Map<String, String> mapper = Map.of(
            "key",          "observation_id",
            "observerName", "observer.fullName",
            "categoryName", "category.name",
            "locationName", "location.name",
            "category",     "category.category_id",
            "location",     "location.location_id"
    );

    @Override
    public String translateField(String field) {
        return mapper.getOrDefault(field, field);
    }

    @Override
    public void applyFilters(SearchBuilder sb) {
        sb.addFuzzyFilter(
                search,
                "observer.fullName", "observedCompany", "category.name", "location.name");
        sb.addFuzzyFilter(
                observerName,
                "observer.fullName");
        sb.addFuzzyFilter(
                observedCompany,
                "observedCompany");
        sb.addFuzzyFilter(
                categoryName,
                "category.name");
        sb.addFuzzyFilter(
                locationName,
                "location.name");
        sb.addOrFilter(
                category,
                "category.category_id");
        sb.addOrFilter(
                location,
                "location.location_id");
        sb.addOrFilter(
                status,
                "status");
        sb.addOrFilter(
                immediateDanger,
                "immediateDanger");
        sb.addRangeBelowFilter(before, "observedAt");
        sb.addRangeAboveFilter(after, "observedAt");
    }

    @Override
    public Link createLinkInternal(LinkRelation linkRel, UriComponentsBuilder builder) {
        addToBuilder(builder, "search", search);
        addToBuilder(builder, "observerName", observerName);
        addToBuilder(builder, "observedCompany", observedCompany);
        addToBuilder(builder, "categoryName", categoryName);
        addToBuilder(builder, "locationName", locationName);
        addToBuilder(builder, "category", category);
        addToBuilder(builder, "location", location);
        addToBuilder(builder, "status", status);
        addToBuilder(builder, "before", before);
        addToBuilder(builder, "after", after);
        return Link.of(builder.toUriString(), linkRel);
    }
}
