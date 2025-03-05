package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.model.dto.SavedLongAndStringValue;
import com.kasirpinter.pos.model.dto.SavedStringAndLongValue;
import com.kasirpinter.pos.repository.ProductCategoryRepository;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.service.DataProjectionService;
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

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public Map<String, Long> countProductByCategoryIds(List<String> idsList) {
        List<SavedStringAndLongValue> queryList = productCategoryRepository.countProductByCategoryIds(idsList);
        return parserSavedStringAndLong(queryList);
    }

    @Override
    public Map<Long, String> findUserNameByIdsMaps(List<Long> idsList) {
        List<SavedLongAndStringValue> queryList = userRepository.findUserNameByIdsMaps(idsList);
        return parserSavedLongAndString(queryList);
    }



    // parser string and long
    private Map<String, Long> parserSavedStringAndLong(List<SavedStringAndLongValue> queryList) {
        Map<String, Long> listIdMap = new HashMap<>();
        for (SavedStringAndLongValue q : queryList) {
            if (!listIdMap.containsKey(q.getKey())) {
                listIdMap.put(q.getKey(), q.getValue());
            }
        }
        return listIdMap;
    }

    // parser long and string
    private Map<Long, String> parserSavedLongAndString(List<SavedLongAndStringValue> queryList) {
        Map<Long, String> listIdMap = new HashMap<>();
        for (SavedLongAndStringValue q : queryList) {
            if (!listIdMap.containsKey(q.getKey())) {
                listIdMap.put(q.getKey(), q.getValue());
            }
        }
        return listIdMap;
    }

}
