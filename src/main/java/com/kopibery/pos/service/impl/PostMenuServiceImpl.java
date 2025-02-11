package com.kopibery.pos.service.impl;

import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.model.projection.AppMenuProjection;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.ProductRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.PostMenuService;
import com.kopibery.pos.util.GlobalConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMenuServiceImpl implements PostMenuService {

    private final ProductRepository productRepository;

    @Override
    public ResultPageResponseDTO<MenuModel.MenuIndexResponse> listMenuIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, String category) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<AppMenuProjection> firstResult = productRepository.findMenuByKeyword(set.keyword(), set.pageable(), category);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<AppMenuProjection> pageResult = productRepository.findMenuByKeyword(set.keyword(), pageable, category);

        // Map the data to the DTOs
        List<MenuModel.MenuIndexResponse> dtos = pageResult.stream().map(this::convertToBackResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    private MenuModel.MenuIndexResponse convertToBackResponse(AppMenuProjection data){
        return new MenuModel.MenuIndexResponse(
                data.getProductId(),
                data.getName(),
                data.getImage(),
                data.getPrice(),
                data.getCategoryId(),
                data.getCategoryName(),
                data.getStock()
        );
    }
}
