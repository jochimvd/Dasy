package xyz.vandijck.safety.backend.service;

import lombok.Data;

import java.util.List;

/**
 * Data transfer object of search results.
 *
 * @param <T> The type of single search result object.
 */
@Data
public class SearchDTO<T>
{
    private List<T> list;
    private int totalPages;
    private int totalElements;

    /**
     * Search information DTO.
     *
     * @param list          The list of search results.
     * @param totalPages    The total amount of page search results.
     * @param totalElements The total amount of elements matching the search.
     */
    public SearchDTO(List<T> list, int totalPages, int totalElements)
    {
        this.list = list;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

}
