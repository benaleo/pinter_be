package com.kopibery.pos.service.impl;

import com.kopibery.pos.entity.ProductCategory;
import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.model.TransactionModel;
import com.kopibery.pos.model.projection.AppDetailMenuOrderProjection;
import com.kopibery.pos.model.projection.AppMenuProjection;
import com.kopibery.pos.model.projection.AppOrderProjection;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.ProductCategoryRepository;
import com.kopibery.pos.repository.ProductRepository;
import com.kopibery.pos.repository.TransactionRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.PostMenuService;
import com.kopibery.pos.util.GlobalConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMenuServiceImpl implements PostMenuService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Value("${app.base.url}")
    private String baseUrl;

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
        List<MenuModel.MenuIndexResponse> dtos = pageResult.stream().map(this::convertSingleMenuResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public ResultPageResponseDTO<MenuModel.OrderIndexResponse> listOrderIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, TransactionType paymentMethod, TransactionStatus paymentStatus) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<AppOrderProjection> firstResult = transactionRepository.findOrderByKeyword(set.keyword(), set.pageable(), paymentMethod, paymentStatus);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<AppOrderProjection> pageResult = transactionRepository.findOrderByKeyword(set.keyword(), pageable, paymentMethod, paymentStatus);

        // Map the data to the DTOs
        List<MenuModel.OrderIndexResponse> dtos = pageResult.stream().map(this::convertSingleOrderResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public List<Map<String, String>> listMenuCategoryIndex() {
        List<ProductCategory> categories = productCategoryRepository.findAllByIsActive(true);
        List<Map<String, String>> responses = new ArrayList<>();

        Map<String, String> response = new HashMap<>();
        response.put("id", null);
        response.put("name", "all");
        responses.add(response);

        for (ProductCategory category : categories) {
            Map<String, String> resp = new HashMap<>();
            resp.put("id", category.getSecureId());
            resp.put("name", category.getName());
            responses.add(resp);
        }

        return responses;
    }

    @Override
    public void updateTransaction(TransactionModel.CreateUpdateRequest item) {

    }

    private MenuModel.MenuIndexResponse convertSingleMenuResponse(AppMenuProjection data){
        return new MenuModel.MenuIndexResponse(
                data.getProductId(),
                data.getName(),
                baseUrl + data.getImage(),
                data.getPrice(),
                data.getCategoryId(),
                data.getCategoryName(),
                data.getStock()
        );
    }

    private MenuModel.OrderIndexResponse convertSingleOrderResponse(AppOrderProjection data){
        List<AppDetailMenuOrderProjection> details = transactionRepository.findOrderDetailByTransactionId(data.getTransaction_id());
        List<MenuModel.DetailsMenuOrder> detailList = details.stream().map(detail ->
                new MenuModel.DetailsMenuOrder(
                        detail.getId(),
                        detail.getName(),
                        baseUrl + detail.getImage(),
                        detail.getPrice(),
                        detail.getQuantity()
        )).collect(Collectors.toList());

        return new MenuModel.OrderIndexResponse(
                data.getTransaction_id(),
                data.getInvoice(),
                data.getCustomer_name(),
                data.getCashier_name(),
                details.isEmpty() ? 0 : details.size(),
                details.isEmpty() ? 0 : details.stream().mapToInt(detail -> detail.getPrice() * detail.getQuantity()).sum(),
                data.getPayment_amount(),
                data.getPayment_status(),
                data.getPayment_method(),
                detailList
        );
    }
}
