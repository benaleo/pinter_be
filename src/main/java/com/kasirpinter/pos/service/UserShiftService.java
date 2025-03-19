package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.UserShiftModel;
import com.kasirpinter.pos.model.UserShiftModel.ShiftAssignedResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface UserShiftService {
    ResultPageResponseDTO<UserShiftModel.ShiftIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    UserShiftModel.ShiftDetailResponse findDataBySecureId(String id);

    UserShiftModel.ShiftDetailResponse saveData(UserShiftModel.ShiftCreateRequest item);

    UserShiftModel.ShiftDetailResponse updateData(String id, UserShiftModel.ShiftUpdateRequest item);

    void deleteData(String id);

    ResultPageResponseDTO<UserShiftModel.ShiftAssignedResponse> listIndexAssigned(Integer pages, Integer limit, String sortBy,
            String direction, String keyword, String shiftId);

    void saveDataAssigned(UserShiftModel.ShiftAssignedRequest item, String shiftId);

    void deleteDataAssigned(String shiftId, String userId);

    ResultPageResponseDTO<ShiftAssignedResponse> listIndexNotAssign(Integer pages, Integer limit, String sortBy,
            String direction, String keyword, String shiftId);
}
