package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.ProductCategory;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.enums.ProductCategoryType;
import com.kasirpinter.pos.model.ProductCategoryModel;
import com.kasirpinter.pos.model.projection.CastKeyValueProjection;
import com.kasirpinter.pos.model.projection.ProductCategoryIndexProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.ProductCategoryRepository;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.DataProjectionService;
import com.kasirpinter.pos.service.ProductCategoryService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kasirpinter.pos.exception.BadRequestException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final DataProjectionService dataProjectionService;
    private final UserRepository userRepository;

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    @Transactional
    public ResultPageResponseDTO<ProductCategoryModel.ProductCategoryIndexResponse> listIndex(Integer pages, Integer limit,
            String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        String roleName = ContextPrincipal.getRoleName();
        String companyId = roleName.equals("SUPERADMIN") ? null : user.getCompany().getSecureId();

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<ProductCategoryIndexProjection> firstResult = productCategoryRepository.findDataByKeyword(set.keyword(),
                set.pageable(), companyId);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<ProductCategoryIndexProjection> pageResult = productCategoryRepository.findDataByKeyword(set.keyword(),
                pageable, companyId);

        // List id
        List<String> idsList = pageResult.stream().map(ProductCategoryIndexProjection::getId)
                .collect(Collectors.toList());
        Map<String, Long> mapCountProducts = dataProjectionService.countProductByCategoryIds(idsList);

        // Map the data to the DTOs
        List<ProductCategoryModel.ProductCategoryIndexResponse> dtos = pageResult.stream().map((c) -> {
            return convertToBackResponse(c, mapCountProducts);
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    @Override
    public ProductCategoryModel.ProductCategoryDetailResponse findDataBySecureId(String id) {
        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);

        return convertToDetail(data);
    }

    @Override
    @Transactional
    public ProductCategoryModel.ProductCategoryIndexResponse saveData(ProductCategoryModel.ProductCategoryCreateRequest item) {
        Long userId = ContextPrincipal.getId();
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        if (item.getName().isEmpty() || item.getName() == null) {
            throw new BadRequestException("Name cannot be empty");
        }

        if (user.getCompany() == null) {
            throw new BadRequestException("Kamu harus terdaftar pada perusahaan");
        }

        ProductCategory newData = new ProductCategory();
        newData.setName(item.getName());
        newData.setIsActive(item.getIsActive());
        newData.setCompany(user.getCompany());
        newData.setType(item.getType() != null ? item.getType() : ProductCategoryType.MENU);

        GlobalConverter.CmsAdminCreateAtBy(newData, userId);
        ProductCategory savedData = productCategoryRepository.save(newData);

        Map<String, Long> mapCountProducts = dataProjectionService
                .countProductByCategoryIds(List.of(savedData.getSecureId()));
        ProductCategoryIndexProjection projection = productCategoryRepository
                .findDataByKeyword(savedData.getSecureId(), Pageable.unpaged(), user.getCompany().getSecureId())
                .getContent().getFirst();
        return convertToBackResponse(projection, mapCountProducts);
    }

    @Override
    @Transactional
    public ProductCategoryModel.ProductCategoryIndexResponse updateData(String id, ProductCategoryModel.ProductCategoryUpdateRequest item) {
        Long userId = ContextPrincipal.getId();
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);
        data.setName(item.getName() != null ? item.getName() : data.getName());
        data.setIsActive(item.getIsActive() != null ? item.getIsActive() : data.getIsActive());
        data.setType(item.getType() != null ? item.getType() : data.getType());

        GlobalConverter.CmsAdminUpdateAtBy(data, userId);
        ProductCategory savedData = productCategoryRepository.save(data);

        Map<String, Long> mapCountProducts = dataProjectionService
                .countProductByCategoryIds(List.of(savedData.getSecureId()));
        ProductCategoryIndexProjection projection = productCategoryRepository
                .findDataByKeyword(savedData.getSecureId(), Pageable.unpaged(), user.getCompany().getSecureId())
                .getContent().getFirst();
        return convertToBackResponse(projection, mapCountProducts);
    }

    @Override
    @Transactional
    public void updateSoftDelete(String id) {
        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);
        productCategoryRepository.updateIsActiveFalseAndIsDeleteTrue(data);
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);
        productCategoryRepository.delete(data);
    }


    //
    //
    //
    //
    //
    // Apps
    //
    //
    //
    //
    //
    @Override
    public ResultPageResponseDTO<ProductCategoryModel.ProductCategoryIndexResponse> listIndexInApp(Integer pages, Integer limit,
            String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<ProductCategoryIndexProjection> firstResult = productCategoryRepository
                .findDataByKeywordInApp(set.keyword(), set.pageable(), user.getCompany().getSecureId());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<ProductCategoryIndexProjection> pageResult = productCategoryRepository
                .findDataByKeywordInApp(set.keyword(), pageable, user.getCompany().getSecureId());

        // List id
        List<String> idsList = pageResult.stream().map(ProductCategoryIndexProjection::getId)
                .collect(Collectors.toList());
        Map<String, Long> mapCountProducts = dataProjectionService.countProductByCategoryIds(idsList);

        // Map the data to the DTOs
        List<ProductCategoryModel.ProductCategoryIndexResponse> dtos = pageResult.stream().map((c) -> {
            return convertToBackResponse(c, mapCountProducts);
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    private ProductCategoryModel.ProductCategoryIndexResponse convertToBackResponse(ProductCategoryIndexProjection data,
            Map<String, Long> mapCountProducts) {
        Long total = mapCountProducts.get(data.getId());

        ProductCategoryModel.ProductCategoryIndexResponse dto = new ProductCategoryModel.ProductCategoryIndexResponse();
        dto.setName(data.getName()); // name
        dto.setTotalProducts(total != null ? total : 0L);
        dto.setType(data.getType());
        dto.setIsActive(data.getIsActive()); // status active

        GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, data.getId(), data.getCreatedAt(),
                data.getUpdatedAt(), data.getCreatedBy(), data.getUpdatedBy());
        return dto;
    }

    private ProductCategoryModel.ProductCategoryDetailResponse convertToDetail(ProductCategory data) {
        return new ProductCategoryModel.ProductCategoryDetailResponse(
                data.getName(),
                data.getType(),
                data.getIsActive());
    }
}
