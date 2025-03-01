package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.UserShiftModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface UserShiftService {
    ResultPageResponseDTO<UserShiftModel.ShiftIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    UserShiftModel.ShiftDetailResponse findDataBySecureId(String id);

    UserShiftModel.ShiftDetailResponse saveData(UserShiftModel.ShiftCreateRequest item);

    UserShiftModel.ShiftDetailResponse updateData(String id, UserShiftModel.ShiftUpdateRequest item);

    void deleteData(String id);
}
