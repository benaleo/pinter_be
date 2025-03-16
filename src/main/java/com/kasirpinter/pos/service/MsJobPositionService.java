package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.JobPositionModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import jakarta.validation.Valid;

public interface MsJobPositionService {
    ResultPageResponseDTO<JobPositionModel.JobPositionIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    JobPositionModel.JobPositionDetailResponse findJobPositionBySecureId(String id);

    JobPositionModel.JobPositionDetailResponse saveData(JobPositionModel.@Valid JobPositionCreateRequest item);

    JobPositionModel.JobPositionDetailResponse updateData(String id, JobPositionModel.@Valid JobPositionUpdateRequest item);

    void deleteData(String id);
}
