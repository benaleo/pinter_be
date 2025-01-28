package com.kopibery.pos.service;

import com.kopibery.pos.model.RoleModel;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface RoleService {
    ResultPageResponseDTO<RoleModel.IndexResponse> listData(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    RoleModel.DetailResponse findDataBySecureId(String id);

    RoleModel.DetailResponse saveData(RoleModel.CreateUpdateRequest item);

    RoleModel.DetailResponse updateData(String id, RoleModel.CreateUpdateRequest item);

    void deleteData(String id);
}
