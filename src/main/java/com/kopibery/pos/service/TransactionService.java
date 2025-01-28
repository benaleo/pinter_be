package com.kopibery.pos.service;

import com.kopibery.pos.model.TransactionModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface TransactionService {
    ResultPageResponseDTO<TransactionModel.IndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword);

    TransactionModel.DetailResponse findDataById(String id);

    TransactionModel.IndexResponse saveData(TransactionModel.CreateUpdateRequest item);

    TransactionModel.IndexResponse updateData(String id, TransactionModel.CreateUpdateRequest item);

    void deleteData(String id);
}
