package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.CompanyCategoryModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface CompanyCategoryService {
    ResultPageResponseDTO<CompanyCategoryModel.CompanyCategoryIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    CompanyCategoryModel.CompanyCategoryDetailResponse findCompanyCategoryBySecureId(String id);

    CompanyCategoryModel.CompanyCategoryDetailResponse saveData(CompanyCategoryModel.CompanyCategoryCreateRequest item);

    CompanyCategoryModel.CompanyCategoryDetailResponse updateData(String id, CompanyCategoryModel.CompanyCategoryUpdateRequest item);

    void deleteData(String id);
}
