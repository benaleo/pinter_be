package com.kasirpinter.pos.service;

import java.util.List;
import java.util.Map;

import com.kasirpinter.pos.model.CompanyModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface CompanyService {
    ResultPageResponseDTO<CompanyModel.CompanyIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, Boolean isParent);

    CompanyModel.CompanyDetailResponse findDataBySecureId(String id);

    CompanyModel.CompanyDetailResponse saveData(CompanyModel.CompanyCreateRequest item);

    CompanyModel.CompanyDetailResponse updateData(String id, CompanyModel.CompanyUpdateRequest item);

    void deleteData(String id);

    // attribute
    List<Map<String, String>> getListInputForm();
}
