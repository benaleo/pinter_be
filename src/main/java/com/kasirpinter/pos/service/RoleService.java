package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.RoleModel;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface RoleService {
    ResultPageResponseDTO<RoleModel.RoleIndexResponse> listData(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    RoleModel.RoleDetailResponse findDataBySecureId(String id);

    RoleModel.RoleDetailResponse saveData(RoleModel.RoleCreateUpdateRequest item);

    RoleModel.RoleDetailResponse updateData(String id, RoleModel.RoleCreateUpdateRequest item);

    void deleteData(String id);
}
