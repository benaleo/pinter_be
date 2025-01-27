package com.kopibery.pos.service;

import com.kopibery.pos.model.UserModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface UserService {
    ResultPageResponseDTO<UserModel.IndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    UserModel.DetailResponse findDataById(String id);

    void saveData(UserModel.CreateRequest item);

    void updateData(String id, UserModel.UpdateRequest item);

    void deleteData(String id);
}
