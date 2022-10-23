package xyz.vandijck.safety.backend.service;

import org.apache.lucene.search.Query;

import java.util.Collection;

/**
 * An interface to help build search queries.
 */
public interface SearchBuilder {

    /**
     * Convert the built search filters to a query.
     *
     * @return The query
     */
    Query toQuery();

    /**
     * Adds a fuzzy filter on one or more fields.
     * Needs to be mostly complete for the edit distance threshold to find a good result.
     * So only works for small typos.
     * Supports wildcards.
     *
     * @param data   The search query
     * @param fields The field(s)
     */
    void addFuzzyFilter(String data, String... fields);

    /**
     * Add a filter for a collection
     *
     * @param data   The search query collection
     * @param fields The field(s)
     */
    void addFuzzyFilter(Collection<String> data, String... fields);

    /**
     * Adds an exact filters for a collection
     * where the field should match one or more of the filters.
     *
     * @param fields The field(s)
     * @param data   The collection search query
     * @param <T>    The type of the collection
     */
    <T> void addOrFilter(Collection<T> data, String... fields);

    /**
     * Adds an exact filter for a collection.
     *
     * @param data   The collection search query
     * @param <T>    The type of the collection
     * @param fields The field(s)
     */
    <T> void addFilter(Collection<T> data, String... fields);

    /**
     * Adds an exact filter.
     *
     * @param data   The collection search query
     * @param <T>    The type of the collection
     * @param fields The field(s)
     */
    <T> void addFilter(T data, String... fields);

    /**
     * Adds a regular filter on a field if the data is not null, not exact matching (star operator).
     *
     * @param data   The search query
     * @param fields The field(s)
     */
    void addFilterStar(String data, String... fields);

    /**
     * Adds a range filter on a field that matches everything above the data,
     * if the data is not null.
     *
     * @param data  The lower bound of the range
     * @param field The field(s)
     */
    <T> void addRangeAboveFilter(T data, String field);

    /**
     * Adds a range filter on a field that matches everything below the data,
     * if the data is not null.
     *
     * @param data  The upper bound of the range
     * @param field The field
     */
    <T> void addRangeBelowFilter(T data, String field);

    /**
     * Add a range filter.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     * @param field The field
     */
    <T> void addRangeFilter(T lower, T upper, String field);

    /**
     * Adds a spatial filter.
     *
     * @param radius    The radius of the space we want to match
     * @param latitude  The latitude of the center
     * @param longitude The longitude of the center
     * @param field     The field
     */
    void addSpatialFilter(Double radius, Double latitude, Double longitude, String field);

}
