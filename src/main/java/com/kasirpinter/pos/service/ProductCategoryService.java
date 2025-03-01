package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.ProductCategoryModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

import java.util.List;
import java.util.Map;

public interface ProductCategoryService {
    ResultPageResponseDTO<ProductCategoryModel.IndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    ProductCategoryModel.DetailResponse findDataBySecureId(String id);

    ProductCategoryModel.IndexResponse saveData(ProductCategoryModel.CreateRequest item);

    ProductCategoryModel.IndexResponse updateData(String id, ProductCategoryModel.UpdateRequest item);

    void updateSoftDelete(String id);

    void deleteData(String id);

    List<Map<String, String>> getListInputForm();


    // Apps
    ResultPageResponseDTO<ProductCategoryModel.IndexResponse> listIndexInApp(Integer pages, Integer limit, String sortBy, String direction, String keyword);

}
