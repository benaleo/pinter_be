package com.kasirpinter.pos.service;

import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.enums.InOutType;
import com.kasirpinter.pos.model.UserModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    ResultPageResponseDTO<UserModel.userIndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    UserModel.userDetailResponse findDataById(String id);

    void saveData(UserModel.userCreateRequest item);

    void updateData(String id, UserModel.userUpdateRequest item);

    void updateAvatar(String id, MultipartFile avatar) throws IOException;

    void deleteData(String id);

    UserModel.UserInfo getUserInfo();

    UserModel.UserInfo getPresenceUserIn(InOutType type);

    UserModel.UserInfo setCompanyModal(Integer value);

    Users findByEmail(String email);

    void assignUserToShift(UserModel.userAssignShiftRequest item);
}
