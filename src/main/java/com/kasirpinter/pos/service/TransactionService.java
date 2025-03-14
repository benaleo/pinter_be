package com.kasirpinter.pos.service;

import com.kasirpinter.pos.model.TransactionModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

public interface TransactionService {
    ResultPageResponseDTO<TransactionModel.TransactionIndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    TransactionModel.TransactionDetailResponse findDataById(String id);

    TransactionModel.TransactionIndexResponse saveData(TransactionModel.TransactionCreateUpdateRequest item);

    TransactionModel.TransactionIndexResponse updateData(String id, TransactionModel.TransactionCreateUpdateRequest item);

    void deleteData(String id);

    TransactionModel.TransactionIndexResponse updateStatusToCancel(String transactionId);
}
