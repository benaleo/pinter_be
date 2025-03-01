package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.RoleModel;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface RoleService {
    ResultPageResponseDTO<RoleModel.IndexResponse> listData(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    RoleModel.DetailResponse findDataBySecureId(String id);

    RoleModel.DetailResponse saveData(RoleModel.CreateUpdateRequest item);

    RoleModel.DetailResponse updateData(String id, RoleModel.CreateUpdateRequest item);

    void deleteData(String id);
}
