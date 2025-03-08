package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.ProductCategoryModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

import java.util.List;
import java.util.Map;

public interface ProductCategoryService {
    ResultPageResponseDTO<ProductCategoryModel.ProductCategoryIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    ProductCategoryModel.ProductCategoryDetailResponse findDataBySecureId(String id);

    ProductCategoryModel.ProductCategoryIndexResponse saveData(ProductCategoryModel.ProductCategoryCreateRequest item);

    ProductCategoryModel.ProductCategoryIndexResponse updateData(String id, ProductCategoryModel.ProductCategoryUpdateRequest item);

    void updateSoftDelete(String id);

    void deleteData(String id);

    List<Map<String, String>> getListInputForm();


    // Apps
    ResultPageResponseDTO<ProductCategoryModel.ProductCategoryIndexResponse> listIndexInApp(Integer pages, Integer limit, String sortBy, String direction, String keyword);

}
