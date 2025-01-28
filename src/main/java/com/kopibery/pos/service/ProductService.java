package com.kopibery.pos.service;

import com.kopibery.pos.model.ProductModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

import java.io.IOException;

public interface ProductService {
    ResultPageResponseDTO<ProductModel.IndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    ProductModel.DetailResponse findDataBySecureId(String id);

    ProductModel.IndexResponse saveData(ProductModel.CreateRequest item) throws IOException;

    ProductModel.IndexResponse updateData(String id, ProductModel.UpdateRequest item) throws IOException;

    void deleteData(String id);
}
