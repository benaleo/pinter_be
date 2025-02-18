package com.kopibery.pos.service;

import com.kopibery.pos.model.UserShiftModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface UserShiftService {
    ResultPageResponseDTO<UserShiftModel.ShiftIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    UserShiftModel.ShiftDetailResponse findDataBySecureId(String id);

    UserShiftModel.ShiftDetailResponse saveData(UserShiftModel.ShiftCreateRequest item);

    UserShiftModel.ShiftDetailResponse updateData(String id, UserShiftModel.ShiftUpdateRequest item);

    void deleteData(String id);
}
