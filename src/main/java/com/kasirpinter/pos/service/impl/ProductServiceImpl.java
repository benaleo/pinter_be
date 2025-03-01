package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.Product;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.ProductModel;
import com.kasirpinter.pos.model.projection.ProductIndexProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.ProductCategoryRepository;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.ProductService;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public ResultPageResponseDTO<ProductModel.ProductIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<ProductIndexProjection> firstResult = productRepository.findDataByKeyword(set.keyword(), set.pageable(), user.getCompany().getSecureId());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<ProductIndexProjection> pageResult = productRepository.findDataByKeyword(set.keyword(), pageable, user.getCompany().getSecureId());

        // Map the data to the DTOs
        List<ProductModel.ProductIndexResponse> dtos = pageResult.stream().map(this::convertToBackResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public ProductModel.DetailResponse findDataBySecureId(String id) {
        Product data = TreeGetEntity.parsingProductByProjection(id, productRepository);

        return convertToDetailProduct(data);
    }

    @Override
    @Transactional
    public ProductModel.ProductIndexResponse saveData(ProductModel.CreateRequest item) throws IOException {
        Long userId = ContextPrincipal.getId();

        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        Product newData = new Product();
        byte[] fileBytes = item.getImage() != null ? item.getImage().getBytes() : null;

        newData.setName(item.getName());
        newData.setPrice(item.getPrice());
        newData.setHppPrice(item.getHppPrice());
        newData.setStock(item.getStock());
        newData.setIsUnlimited(item.getIsUnlimited());
        newData.setIsUpSale(item.getIsUpSale());
        newData.setIsActive(item.getIsActive());
        newData.setCategory(TreeGetEntity.parsingProductCategoryByProjection(item.getCategoryId(), productCategoryRepository));
        newData.setImage(item.getImage() != null ? fileBytes : null);
        newData.setImageName(item.getImage() != null ? item.getImage().getOriginalFilename() : null);

        GlobalConverter.CmsAdminCreateAtBy(newData, userId);
        Product savedData = productRepository.save(newData);

        savedData.setImageUrl(item.getImage() != null ? "/get/file/product/" + savedData.getSecureId() : null);
        productRepository.save(savedData);

        ProductIndexProjection projection = productRepository.findDataByKeyword(savedData.getSecureId(), Pageable.unpaged(), user.getCompany().getSecureId()).getContent().getFirst();
        return convertToBackResponse(projection);
    }

    @Override
    @Transactional
    public ProductModel.ProductIndexResponse updateData(String id, ProductModel.UpdateRequest item) throws IOException {
        Long userId = ContextPrincipal.getId();

        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        Product data = TreeGetEntity.parsingProductByProjection(id, productRepository);
        byte[] fileBytes = item.getImage() != null && item.getImage().getOriginalFilename() != null ? item.getImage().getBytes() : null;

        data.setName(item.getName() != null ? item.getName() : data.getName());
        data.setPrice(item.getPrice() != null ? item.getPrice() : data.getPrice());
        data.setHppPrice(item.getHppPrice() != null ? item.getHppPrice() : data.getHppPrice());
        data.setStock(item.getStock() != null ? item.getStock() : data.getStock());
        data.setIsUnlimited(item.getIsUnlimited() != null ? item.getIsUnlimited() : data.getIsUnlimited());
        data.setIsUpSale(item.getIsUpSale() != null ? item.getIsUpSale() : data.getIsUpSale());
        data.setIsActive(item.getIsActive() != null ? item.getIsActive() : data.getIsActive());
        data.setCategory(item.getCategoryId() != null ? TreeGetEntity.parsingProductCategoryByProjection(item.getCategoryId(), productCategoryRepository) : data.getCategory());
        data.setImage(item.getImage() != null ? fileBytes : data.getImage());
        data.setImageName(item.getImage() != null ? item.getImage().getOriginalFilename() : data.getImageName());

        GlobalConverter.CmsAdminUpdateAtBy(data, userId);
        Product savedData = productRepository.save(data);

        savedData.setImageUrl(item.getImage() != null ? "/get/file/product/" + savedData.getSecureId() : null);
        productRepository.save(savedData);

        ProductIndexProjection projection = productRepository.findDataByKeyword(savedData.getSecureId(), Pageable.unpaged(), user.getCompany().getSecureId()).getContent().getFirst();
        return convertToBackResponse(projection);
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        Product data = TreeGetEntity.parsingProductByProjection(id, productRepository);
        log.info("productId is : {} with secure id :{}", data.getId(), data.getSecureId());
        productRepository.updateIsActiveFalseAndIsDeletedTrue(data);
    }

    //
    //
    //
    //
    //
    //
    //
    // Apps
    @Override
    public ResultPageResponseDTO<ProductModel.ProductIndexResponse> listIndexApp(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<ProductIndexProjection> firstResult = productRepository.findDataByKeywordInApps(set.keyword(), set.pageable(), user.getCompany().getSecureId());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<ProductIndexProjection> pageResult = productRepository.findDataByKeywordInApps(set.keyword(), pageable, user.getCompany().getSecureId());

        // Map the data to the DTOs
        List<ProductModel.ProductIndexResponse> dtos = pageResult.stream().map(this::convertToBackResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    private ProductModel.ProductIndexResponse convertToBackResponse(ProductIndexProjection data) {
        ProductModel.ProductIndexResponse dto = new ProductModel.ProductIndexResponse();
        dto.setName(data.getName());                   // name
        dto.setPrice(data.getPrice());                 // price
        dto.setHppPrice(data.getHppPrice());           // hpp price
        dto.setIsActive(data.getIsActive());           // status active
        dto.setCategoryName(data.getCategoryName());   // category name
        dto.setStock(data.getStock());                 // stock
        dto.setIsUnlimited(data.getIsUnlimited());     // unlimited
        dto.setIsUpSale(data.getIsUpSale());           // up sale
        dto.setImage(
                data.getImage() != null ? baseUrl + "/get/file/product/" + data.getId() : null
        );                                          // image

        GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, data.getId(), data.getCreatedAt(), data.getUpdatedAt(), data.getCreatedBy(), data.getUpdatedBy());
        return dto;
    }

    private ProductModel.DetailResponse convertToDetailProduct(Product data) {
        return new ProductModel.DetailResponse(
                data.getName(),
                data.getPrice(),
                data.getHppPrice(),
                data.getIsActive(),
                data.getCategory().getSecureId(),
                data.getCategory().getName(),
                data.getStock(),
                data.getIsUnlimited(),
                data.getIsUpSale(),
                data.getImage() != null ? baseUrl + "/get/file/product/" + data.getSecureId() : null
        );
    }
}
