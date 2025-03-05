package com.kasirpinter.pos.service;

import java.util.List;
import java.util.Map;

public interface DataProjectionService {

    // product
    Map<String,Long> countProductByCategoryIds(List<String> idsList);

    // user
    Map<Long,String> findUserNameByIdsMaps(List<Long> idsList);

}
