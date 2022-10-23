package xyz.vandijck.safety.backend.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Base search request class.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class SearchRequest extends BaseRequest
{
    @Range(min = 0)
    protected Integer page;
    @Range(min = 1, max = 500)
    protected Integer size;
    protected List<String> orderBy;
    protected SortOrdering orderDir;



    /**
     * Creates a link for HATEOAS.
     * See: <a href="https://github.com/spring-projects/spring-hateoas/issues/496">...</a>
     *
     * @param linkRel Link relation (see HATEOAS)
     * @param builder The URI builder
     * @return The link
     */
    public final Link createLink(LinkRelation linkRel, UriComponentsBuilder builder)
    {
        return createLink(linkRel, builder, getPage());
    }

    /**
     * Creates a link for HATEOAS, with an explicit page parameter.
     *
     * @param linkRel Link relation (see HATEOAS)
     * @param builder The URI builder
     * @param page    The page number
     * @return The link
     */
    public final Link createLink(LinkRelation linkRel, UriComponentsBuilder builder, int page)
    {
        addToBuilder(builder, "page", page);
        addToBuilder(builder, "size", size);
        addToBuilder(builder, "orderBy", orderBy);
        addToBuilder(builder, "orderDir", orderDir);
        return createLinkInternal(linkRel, builder);
    }

    /**
     * Gets the current page of the search request.
     *
     * @return The current page
     */
    public int getPage()
    {
        return Objects.requireNonNullElse(page, 0);
    }

    /**
     * Gets the size of the search request: how many items at once.
     *
     * @return The size
     */
    public int getSize()
    {
        return Objects.requireNonNullElse(size, 20);
    }
}
