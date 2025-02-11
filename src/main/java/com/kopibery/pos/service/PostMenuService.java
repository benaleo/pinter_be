package com.kopibery.pos.service;

import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.response.ResultPageResponseDTO;

public interface PostMenuService {

    ResultPageResponseDTO<MenuModel.MenuIndexResponse> listMenuIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, String category);
}
