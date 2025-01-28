package com.kopibery.pos.service.impl;

import com.kopibery.pos.entity.ProductCategory;
import com.kopibery.pos.model.ProductCategoryModel;
import com.kopibery.pos.model.projection.ProductCategoryIndexProjection;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.ProductCategoryRepository;
import com.kopibery.pos.repository.ProductRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.DataProjectionService;
import com.kopibery.pos.service.ProductCategoryService;
import com.kopibery.pos.util.ContextPrincipal;
import com.kopibery.pos.util.GlobalConverter;
import com.kopibery.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final DataProjectionService dataProjectionService;

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public ResultPageResponseDTO<ProductCategoryModel.IndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<ProductCategoryIndexProjection> firstResult = productCategoryRepository.findDataByKeyword(set.keyword(), set.pageable());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<ProductCategoryIndexProjection> pageResult = productCategoryRepository.findDataByKeyword(set.keyword(), pageable);

        // List id
        List<String> idsList = pageResult.stream().map(ProductCategoryIndexProjection::getId).collect(Collectors.toList());
        Map<String, Long> mapCountProducts = dataProjectionService.countProductByCategoryIds(idsList);

        // Map the data to the DTOs
        List<ProductCategoryModel.IndexResponse> dtos = pageResult.stream().map((c) -> {
            return convertToBackResponse(c, mapCountProducts);
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public ProductCategoryModel.DetailResponse findDataBySecureId(String id) {
        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);

        return convertToDetail(data);
    }

    @Override
    @Transactional
    public ProductCategoryModel.IndexResponse saveData(ProductCategoryModel.CreateRequest item) {
        Long userId = ContextPrincipal.getId();

        ProductCategory newData = new ProductCategory();
        newData.setName(item.getName());
        newData.setIsActive(item.getIsActive());

        GlobalConverter.CmsAdminCreateAtBy(newData, userId);
        ProductCategory savedData = productCategoryRepository.save(newData);

        Map<String, Long> mapCountProducts = dataProjectionService.countProductByCategoryIds(List.of(savedData.getSecureId()));
        ProductCategoryIndexProjection projection = productCategoryRepository.findDataByKeyword(savedData.getSecureId(), Pageable.unpaged()).getContent().getFirst();
        return convertToBackResponse(projection, mapCountProducts);
    }

    @Override
    @Transactional
    public ProductCategoryModel.IndexResponse updateData(String id, ProductCategoryModel.UpdateRequest item) {
        Long userId = ContextPrincipal.getId();

        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);
        data.setName(item.getName());
        data.setIsActive(item.getIsActive());

        GlobalConverter.CmsAdminUpdateAtBy(data, userId);
        ProductCategory savedData = productCategoryRepository.save(data);

        Map<String, Long> mapCountProducts = dataProjectionService.countProductByCategoryIds(List.of(savedData.getSecureId()));
        ProductCategoryIndexProjection projection = productCategoryRepository.findDataByKeyword(savedData.getSecureId(), Pageable.unpaged()).getContent().getFirst();
        return convertToBackResponse(projection, mapCountProducts);
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        ProductCategory data = TreeGetEntity.parsingProductCategoryByProjection(id, productCategoryRepository);

        productRepository.updateProductCategoryToNull(data);
        productCategoryRepository.delete(data);
    }

    private ProductCategoryModel.IndexResponse convertToBackResponse(ProductCategoryIndexProjection data, Map<String, Long> mapCountProducts) {
        Long total = mapCountProducts.get(data.getId());

        ProductCategoryModel.IndexResponse dto = new ProductCategoryModel.IndexResponse();
        dto.setName(data.getName());                   // name
        dto.setTotalProducts(total != null ? total : 0L);
        dto.setIsActive(data.getIsActive());           // status active

        GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, data.getId(), data.getCreatedAt(), data.getUpdatedAt(), data.getCreatedBy(), data.getUpdatedBy());
        return dto;
    }

    private ProductCategoryModel.DetailResponse convertToDetail(ProductCategory data) {
        return new ProductCategoryModel.DetailResponse(
                data.getName(),
                data.getIsActive()
        );
    }
}
