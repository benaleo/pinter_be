package com.kopibery.pos.service;

import com.kopibery.pos.model.CompanyModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface CompanyService {
    ResultPageResponseDTO<CompanyModel.CompanyIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, Boolean isParent);

    CompanyModel.CompanyDetailResponse findDataBySecureId(String id);

    CompanyModel.CompanyDetailResponse saveData(CompanyModel.CompanyCreateRequest item);

    CompanyModel.CompanyDetailResponse updateData(String id, CompanyModel.CompanyUpdateRequest item);

    void deleteData(String id);
}
