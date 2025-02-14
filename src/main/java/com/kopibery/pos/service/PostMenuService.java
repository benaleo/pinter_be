package com.kopibery.pos.service;

import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.model.TransactionModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

import java.util.List;
import java.util.Map;

public interface PostMenuService {

    ResultPageResponseDTO<MenuModel.MenuIndexResponse> listMenuIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, String category);

    ResultPageResponseDTO<MenuModel.OrderIndexResponse> listOrderIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, TransactionType paymentMethod, TransactionStatus paymentStatus);

    List<Map<String, String>> listMenuCategoryIndex();

    void updateTransaction(TransactionModel.CreateUpdateRequest item);
}
