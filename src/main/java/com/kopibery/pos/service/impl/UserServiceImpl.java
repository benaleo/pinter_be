package com.kopibery.pos.service.impl;

import com.kopibery.pos.model.UserModel;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    public ResultPageResponseDTO<UserModel.IndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        return null;
    }

    @Override
    public UserModel.DetailResponse findDataById(String id) {
        return null;
    }

    @Override
    public void saveData(UserModel.CreateRequest item) {

    }

    @Override
    public void updateData(String id, UserModel.UpdateRequest item) {

    }

    @Override
    public void deleteData(String id) {

    }
}
