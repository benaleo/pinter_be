package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.TransactionModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface TransactionService {
    ResultPageResponseDTO<TransactionModel.IndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    TransactionModel.DetailResponse findDataById(String id);

    TransactionModel.IndexResponse saveData(TransactionModel.CreateUpdateRequest item);

    TransactionModel.IndexResponse updateData(String id, TransactionModel.CreateUpdateRequest item);

    void deleteData(String id);

    TransactionModel.IndexResponse updateStatusToCancel(String transactionId);
}
