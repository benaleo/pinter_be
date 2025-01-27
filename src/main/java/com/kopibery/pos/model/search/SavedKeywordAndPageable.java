package com.kopibery.pos.model.search;

import org.springframework.data.domain.Pageable;

public record SavedKeywordAndPageable(
    String keyword,
    Pageable pageable
) {
}
