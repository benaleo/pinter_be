package com.kopibery.pos.response;

import com.kopibery.pos.util.PaginationUtil;
import org.springframework.data.domain.Page;

import java.util.List;

public class PageCreateReturn {

    public static ResultPageResponseDTO create(Page<?> pageResult, List<?> dtos) {
        int currentPage = pageResult.getNumber();
        int totalPages = pageResult.getTotalPages();

        // Explicitly box primitive types to their wrapper equivalents
        Long totalElements = pageResult.getTotalElements(); // Autoboxing should work here
        Integer currentPageBoxed = currentPage;
        Integer prevPage = currentPage >= 1 ? currentPage - 1 : null;
        Integer nextPage = currentPage < totalPages - 1 ? currentPage + 1 : null;
        Integer firstPage = 0;
        Integer lastPage = totalPages - 1 == 0 ? null : totalPages - 1;
        Integer pageSize = pageResult.getSize();

        // Resolve the generic type for the list
        @SuppressWarnings("unchecked")
        List<Object> resolvedDtos = (List<Object>) dtos;

        return PaginationUtil.createResultPageDTO(
                totalElements,
                resolvedDtos,
                currentPageBoxed,
                prevPage,
                nextPage,
                firstPage,
                lastPage,
                pageSize
        );
    }
}
