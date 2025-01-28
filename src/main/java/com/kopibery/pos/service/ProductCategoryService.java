package com.kopibery.pos.service;

import com.kopibery.pos.model.ProductCategoryModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface ProductCategoryService {
    ResultPageResponseDTO<ProductCategoryModel.IndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    ProductCategoryModel.DetailResponse findDataBySecureId(String id);

    ProductCategoryModel.IndexResponse saveData(ProductCategoryModel.CreateRequest item);

    ProductCategoryModel.IndexResponse updateData(String id, ProductCategoryModel.UpdateRequest item);

    void deleteData(String id);
}
