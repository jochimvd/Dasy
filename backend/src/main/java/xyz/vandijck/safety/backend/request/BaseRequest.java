package xyz.vandijck.safety.backend.request;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.vandijck.safety.backend.service.SearchBuilder;

import java.util.Collection;

/**
 * Base request class.
 */
public abstract class BaseRequest {

    /**
     * Should add a filter for "archived"?
     *
     * @return Whether a filter should be added for the "archived" flag.
     */
    public boolean shouldAddArchiveFilter() {
        return true;
    }

    /**
     * Translate field to real name if needed.
     * Useful for nested fields.
     *
     * @param field The field name
     * @return The translated field name
     */
    public String translateField(String field)
    {
        return field;
    }

    /**
     * Applies the filters to the search builder.
     *
     * @param sb The search builder
     */
    public abstract void applyFilters(SearchBuilder sb);

    /**
     * Create linker internal handler
     *
     * @param linkRel Link relation (see HATEOAS)
     * @param builder The URI builder
     * @return The link
     */
    protected abstract Link createLinkInternal(LinkRelation linkRel, UriComponentsBuilder builder);

    /**
     * Adds a value to a URI builder if not null.
     *
     * @param builder The URI builder
     * @param param   The parameter name
     * @param value   The value
     */
    protected final void addToBuilder(UriComponentsBuilder builder, String param, Object value)
    {
        if (value != null)
            if (value instanceof Collection) {
                builder.queryParam(param, StringUtils.collectionToCommaDelimitedString((Collection<?>) value));
            } else {
                builder.queryParam(param, value);
            }
    }

}
