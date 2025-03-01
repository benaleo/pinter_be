package com.kasirpinter.pos.model.search;

import org.springframework.data.domain.Pageable;

public record SavedKeywordAndPageable(
    String keyword,
    Pageable pageable
) {
}
