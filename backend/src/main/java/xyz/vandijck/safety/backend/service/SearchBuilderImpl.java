package xyz.vandijck.safety.backend.service;

import org.apache.lucene.search.Query;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.Unit;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Default search builder implementation, see {@link SearchBuilder} for more explanation.
 */
public final class SearchBuilderImpl implements SearchBuilder {
    private QueryBuilder qb;
    private BooleanJunction<?> bj;

    /**
     * Creates a new search builder.
     *
     * @param qb The query builder
     */
    public SearchBuilderImpl(QueryBuilder qb) {
        this.qb = qb;
        this.bj = qb.bool();
    }

    /**
     * Converts a boolean junction to a query.
     *
     * @param bj The boolean junction.
     * @return The query.
     */
    private Query toQuery(BooleanJunction<?> bj) {
        // Not allowed to make an empty query, so this works around that issue.
        Query builtQuery;
        if (bj.isEmpty())
            builtQuery = qb.all().createQuery();
        else
            builtQuery = bj.createQuery();

        return builtQuery;
    }

    @Override
    public Query toQuery() {
        return toQuery(bj);
    }

    /**
     * Guard to add filter query to boolean junction
     *
     * @param data The data
     * @param act  The supplier of the query
     * @param <T>  The type of the data
     */
    private <T> void addFilterGuarded(T data, Supplier<Query> act) {
        if (data != null) {
            try {
                bj = bj.must(act.get());
            } catch (EmptyQueryException ignored) {
                // Ignore because the query will be empty and be a fetch all instead. So no issue.
            }
        }
    }

    @Override
    public void addFuzzyFilter(String data, String... fields) {
        addFilterGuarded(data, () -> {
            // The Hibernate search index works in lowercase as set by our custom analyzer.
            // By changing the input to lowercase, we have case-insensitive search.
            String processed = data.toLowerCase();
            return qb.bool().should(
                    qb.keyword().wildcard().onFields(fields).matching("*" + processed + "*").createQuery()
            ).should(
                    qb.keyword().fuzzy().onFields(fields).matching(processed).createQuery()
            ).createQuery();
        });
    }

    @Override
    public void addFuzzyFilter(Collection<String> data, String... fields) {
        if (data != null) {
            for (String s : data) {
                addFuzzyFilter(s, fields);
            }
        }
    }

    @Override
    public <T> void addOrFilter(Collection<T> data, String... fields) {
        if (data != null && !data.isEmpty()) {
            BooleanJunction<?> nbj = qb.bool();
            for (T t: data) {
                nbj = nbj.should(qb.keyword().wildcard().onFields(fields).matching(t).createQuery());
            }
            final Query query = toQuery(nbj);
            addFilterGuarded(data, () -> query);
        }
    }

    @Override
    public <T> void addFilter(Collection<T> data, String... fields) {
        if (data != null) {
            for (T t : data) {
                addFilter(t, fields);
            }
        }
    }

    @Override
    public <T> void addFilter(T data, String... fields) {
        addFilterGuarded(data, () -> qb.keyword().wildcard().onFields(fields).matching(data).createQuery());
    }

    @Override
    public void addFilterStar(String data, String... fields) {
        if (data != null) {
            // See fuzzy filter.
            addFilterGuarded(data, () -> qb.keyword().wildcard().onFields(fields)
                    .matching("*" + data.toLowerCase() + "*").createQuery());
        }
    }

    @Override
    public <T> void addRangeAboveFilter(T data, String field) {
        addFilterGuarded(data, () -> qb.range().onField(field).above(data).createQuery());
    }

    @Override
    public <T> void addRangeBelowFilter(T data, String field) {
        addFilterGuarded(data, () -> qb.range().onField(field).below(data).createQuery());
    }

    @Override
    public void addSpatialFilter(Double radius, Double latitude, Double longitude, String field) {
        if (radius != null && latitude != null && longitude != null) {
            bj = bj.must(qb.spatial().onField(field).within(radius, Unit.KM)
                    .ofLatitude(latitude).andLongitude(longitude).createQuery());
        }
    }

    @Override
    public <T> void addRangeFilter(T lower, T upper, String field) {
        if (lower != null && upper != null) {
            try {
                bj = bj.must(qb.range().onField(field).from(lower).to(upper).createQuery());
            } catch (EmptyQueryException ignored) {
                // Ignore because the query will be empty and be a fetch all instead. So no issue.
            }
        }
    }
}
