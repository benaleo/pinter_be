package com.kopibery.pos.util;

import com.kopibery.pos.response.ResultPageResponseDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaginationUtil {

    public static <T> ResultPageResponseDTO<T> createResultPageDTO(
            Long totalItems,
            List<T> dtos,
            Integer currentPage,
            Integer prevPage,
            Integer nextPage,
            Integer firstPage,
            Integer lastPage,
            Integer perPage
    ) {
        ResultPageResponseDTO<T> result = new ResultPageResponseDTO<T>();
        result.setCurrentPage(currentPage);
        result.setPrevPage(prevPage);
        result.setNextPage(nextPage);
        result.setFirstPage(firstPage);
        result.setLastPage(lastPage);
        result.setPerPage(perPage);
        result.setTotalItems(totalItems);
        result.setResult(dtos);
        return result;
    }

    public static Sort.Direction getSortBy(String sortBy) {
        if (sortBy.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

}
