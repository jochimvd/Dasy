package xyz.vandijck.safety.backend.service;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortContext;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import xyz.vandijck.safety.backend.request.BaseRequest;
import xyz.vandijck.safety.backend.request.SearchRequest;
import xyz.vandijck.safety.backend.request.SortOrdering;

import javax.persistence.EntityManager;

public final class SearchHelper
{
    // unused constructor
    private SearchHelper(){}
    
    /**
     * Find all matching.
     *
     * @param request       Search request.
     * @param entityManager Entity manager.
     * @param clazz         The class of the entity to search for.
     * @param <T>           The entity type to search for.
     * @return A search result data transfer object.
     */
    public static <T> SearchDTO<T> findAll(SearchRequest request, EntityManager entityManager, Class<T> clazz)
    {
        FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(clazz).get();

        // Convert the template to an actual search query on an entity.
        int size = request.getSize();
        FullTextQuery query = createQuery(request, ftem, qb, clazz);
        int totalElements = query.getResultSize();
        int totalPages = (totalElements + size - 1) / size;

        // Pagination
        query.setMaxResults(size);
        query.setFirstResult(request.getPage() * size);

        // Setup sorting fields and sorting direction
        if (request.getOrderBy() != null && !request.getOrderBy().isEmpty())
        {
            SortContext sc = qb.sort();
            SortFieldContext sfc = null;

            for (String field : request.getOrderBy())
            {
                field = request.translateField(field);

                if (sfc == null)
                    sfc = sc.byField(field);
                else
                    sfc = sfc.andByField(field);

                if (request.getOrderDir() == SortOrdering.ASC)
                    sfc = sfc.asc();
                else if (request.getOrderDir() == SortOrdering.DESC)
                    sfc = sfc.desc();
            }

            assert sfc != null; // Cannot fail, because otherwise this block wouldn't be executing.
            query.setSort(sfc.createSort());
        }

        // Hibernate design: getResultList cannot be used in a generic way, which is why we get a warning.
        return new SearchDTO<>(query.getResultList(), totalPages, totalElements);
    }

    public static <T> PageImpl<T> findAll(SearchRequest request, Pageable pageable, EntityManager entityManager, Class<T> clazz)
    {
        FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(clazz).get();

        // Convert the template to an actual search query on an entity.
        int size = pageable.getPageSize();
        FullTextQuery query = createQuery(request, ftem, qb, clazz);
        int totalElements = query.getResultSize();

        // Pagination
        query.setMaxResults(size);
        query.setFirstResult(pageable.getPageNumber() * size);

        // Setup sorting fields and sorting direction
        if (pageable.getSort().isSorted())
        {
            SortContext sc = qb.sort();
            SortFieldContext sfc = null;

            for (Sort.Order order : pageable.getSort())
            {
                // TODO check if the field is valid
                String field = request.translateField(order.getProperty());

                if (sfc == null)
                    sfc = sc.byField(field);
                else
                    sfc = sfc.andByField(field);

                if (order.isAscending())
                    sfc = sfc.asc();
                else if (order.isDescending())
                    sfc = sfc.desc();
            }

            assert sfc != null; // Cannot fail, because otherwise this block wouldn't be executing.
            query.setSort(sfc.createSort()); // TODO maybe also sort by score? sfc.andByScore().createSort()
        }

        // Hibernate design: getResultList cannot be used in a generic way, which is why we get a warning.
        return new PageImpl<>(query.getResultList(), pageable, totalElements);
    }

    /**
     * Create query by applying the filters of the given request.
     *
     * @param request Search request or report request.
     * @param ftem    Full text entity manager.
     * @param qb      Query builder.
     * @param clazz   The class of the entity to be queried.
     * @param <T>     The entity type to be queried.
     * @return The created full text query.
     */
    private static <T> FullTextQuery createQuery(BaseRequest request, FullTextEntityManager ftem, QueryBuilder qb, Class<T> clazz) {
        SearchBuilder sb = new SearchBuilderImpl(qb);
        if (request.shouldAddArchiveFilter()) {
            sb.addFilter(false, "archived");
        }
        request.applyFilters(sb);
        Query builtQuery = sb.toQuery();
        return ftem.createFullTextQuery(builtQuery, clazz);
    }
}
