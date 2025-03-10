package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.ProductCategory;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.enums.TransactionStatus;
import com.kasirpinter.pos.enums.TransactionType;
import com.kasirpinter.pos.model.MenuModel;
import com.kasirpinter.pos.model.TransactionModel;
import com.kasirpinter.pos.model.projection.AppDetailMenuOrderProjection;
import com.kasirpinter.pos.model.projection.AppMenuProjection;
import com.kasirpinter.pos.model.projection.AppOrderProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.ProductCategoryRepository;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.TransactionRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.PosService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMenuServiceImpl implements PosService {

    private final UserRepository userRepository;
    
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public ResultPageResponseDTO<MenuModel.MenuIndexResponse> listMenuIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, String category) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        Company company = user.getCompany();

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<AppMenuProjection> firstResult = productRepository.findMenuByKeyword(set.keyword(), set.pageable(), category, company.getSecureId());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<AppMenuProjection> pageResult = productRepository.findMenuByKeyword(set.keyword(), pageable, category, company.getSecureId());

        // Map the data to the DTOs
        List<MenuModel.MenuIndexResponse> dtos = pageResult.stream().map(this::convertSingleMenuResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public ResultPageResponseDTO<MenuModel.OrderIndexResponse> listOrderIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, TransactionType paymentMethod, TransactionStatus paymentStatus) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        Company company = user.getCompany();

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<AppOrderProjection> firstResult = transactionRepository.findOrderByKeyword(set.keyword(), set.pageable(), paymentMethod, paymentStatus, company.getSecureId());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<AppOrderProjection> pageResult = transactionRepository.findOrderByKeyword(set.keyword(), pageable, paymentMethod, paymentStatus, company.getSecureId());

        // Map the data to the DTOs
        List<MenuModel.OrderIndexResponse> dtos = pageResult.stream().map(this::convertSingleOrderResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    @Transactional
    public List<Map<String, String>> listMenuCategoryIndex() {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        List<ProductCategory> categories = productCategoryRepository.findAllByIsActiveAndCompanyId(true, user.getCompany().getSecureId());
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
                data.getCreated_at().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                detailList
        );
    }
}
