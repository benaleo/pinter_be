package com.kasirpinter.pos.service;

import com.kasirpinter.pos.enums.TransactionStatus;
import com.kasirpinter.pos.enums.TransactionType;
import com.kasirpinter.pos.model.MenuModel;
import com.kasirpinter.pos.model.TransactionModel;
import com.kasirpinter.pos.response.ResultPageResponseDTO;

import java.util.List;
import java.util.Map;

public interface PosService {

    ResultPageResponseDTO<MenuModel.MenuIndexResponse> listMenuIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, String category);

    ResultPageResponseDTO<MenuModel.OrderIndexResponse> listOrderIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, TransactionType paymentMethod, TransactionStatus paymentStatus);

    List<Map<String, String>> listMenuCategoryIndex();

    void updateTransaction(TransactionModel.CreateUpdateRequest item);
}
