package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.ProductModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

import java.io.IOException;

public interface ProductService {
    ResultPageResponseDTO<ProductModel.ProductIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    ProductModel.DetailResponse findDataBySecureId(String id);

    ProductModel.ProductIndexResponse saveData(ProductModel.CreateRequest item) throws IOException;

    ProductModel.ProductIndexResponse updateData(String id, ProductModel.UpdateRequest item) throws IOException;

    void deleteData(String id);

    ResultPageResponseDTO<ProductModel.ProductIndexResponse> listIndexApp(Integer pages, Integer limit, String sortBy, String direction, String keyword);
}
