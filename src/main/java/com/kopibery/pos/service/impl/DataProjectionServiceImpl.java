package com.kopibery.pos.service.impl;

import com.kopibery.pos.model.dto.SavedStringAndLongValue;
import com.kopibery.pos.repository.ProductCategoryRepository;
import com.kopibery.pos.repository.ProductRepository;
import com.kopibery.pos.service.DataProjectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataProjectionServiceImpl implements DataProjectionService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public Map<String, Long> countProductByCategoryIds(List<String> idsList) {
        List<SavedStringAndLongValue> queryList = productCategoryRepository.countProductByCategoryIds(idsList);
        return parserSavedStringAndLong(queryList);
    }


    // parser
    private Map<String, Long> parserSavedStringAndLong(List<SavedStringAndLongValue> queryList) {
        Map<String, Long> listIdMap = new HashMap<>();
        for (SavedStringAndLongValue q : queryList) {
            if (!listIdMap.containsKey(q.getKey())) {
                listIdMap.put(q.getKey(), q.getValue());
            }
        }
        return listIdMap;
    }
}
