package com.kasirpinter.pos.service;

import java.util.List;
import java.util.Map;

import com.kasirpinter.pos.enums.ProductCategoryTypeInput;

public interface InputAttributeService {

    List<Map<String, String>> getListCompany();

    List<Map<String, String>> getListProductCategory(ProductCategoryTypeInput type);

}