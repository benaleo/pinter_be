package com.kopibery.pos.service;

import com.kopibery.pos.model.CompanyModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface CompanyService {
    ResultPageResponseDTO<CompanyModel.IndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, Boolean isParent);

    CompanyModel.DetailResponse findDataBySecureId(String id);

    CompanyModel.DetailResponse saveData(CompanyModel.CreateRequest item);

    CompanyModel.DetailResponse updateData(String id, CompanyModel.UpdateRequest item);

    void deleteData(String id);
}
