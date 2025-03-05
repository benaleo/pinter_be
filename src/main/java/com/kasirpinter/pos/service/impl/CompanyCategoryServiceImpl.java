package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.CompanyCategory;
import com.kasirpinter.pos.model.CompanyCategoryModel;
import com.kasirpinter.pos.model.projection.CompanyCategoryIndexProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.CompanyCategoryRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.CompanyCategoryService;
import com.kasirpinter.pos.service.DataProjectionService;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyCategoryServiceImpl implements CompanyCategoryService {

    private final UserRepository userRepository;
    private final DataProjectionService dataProjectionService;

    private final CompanyCategoryRepository categoryRepository;

    @Override
    public ResultPageResponseDTO<CompanyCategoryModel.CompanyCategoryIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<CompanyCategoryIndexProjection> firstResult = categoryRepository.findDataByKeyword(set.keyword(), set.pageable());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<CompanyCategoryIndexProjection> pageResult = categoryRepository.findDataByKeyword(set.keyword(), pageable);

        List<Long> createdByIds = pageResult.stream().map(CompanyCategoryIndexProjection::getCreatedBy).collect(Collectors.toList());
        List<Long> updatedByIds = pageResult.stream().map(CompanyCategoryIndexProjection::getUpdatedBy).collect(Collectors.toList());
        Map<Long, String> createdBys = dataProjectionService.findUserNameByIdsMaps(createdByIds);
        Map<Long, String> updatedBys = dataProjectionService.findUserNameByIdsMaps(updatedByIds);

        // Map the data to the DTOs
        List<CompanyCategoryModel.CompanyCategoryIndexResponse> dtos = pageResult.stream().map((c) -> {
            CompanyCategoryModel.CompanyCategoryIndexResponse dto = new CompanyCategoryModel.CompanyCategoryIndexResponse();
            dto.setName(c.getName());
            dto.setCategory(c.getCategory());

            GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, c.getId(), c.getCreatedAt(), c.getUpdatedAt(),
                    createdBys.get(c.getCreatedBy()), updatedBys.get(c.getUpdatedBy()));
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    @Override
    public CompanyCategoryModel.CompanyCategoryDetailResponse findCompanyCategoryBySecureId(String id) {
        CompanyCategory data = TreeGetEntity.parsingCompanyCategoryByProjection(id, categoryRepository);
        return toResponse(data);
    }

    @Override
    public CompanyCategoryModel.CompanyCategoryDetailResponse saveData(CompanyCategoryModel.CompanyCategoryCreateRequest item) {
        CompanyCategory newData = new CompanyCategory();
        newData.setName(item.name());
        newData.setCategory(item.category());
        newData.setIsActive(item.isActive());
        newData = categoryRepository.save(newData);
        return toResponse(newData);
    }

    @Override
    public CompanyCategoryModel.CompanyCategoryDetailResponse updateData(String id, CompanyCategoryModel.CompanyCategoryUpdateRequest item) {
        CompanyCategory data = TreeGetEntity.parsingCompanyCategoryByProjection(id, categoryRepository);
        data.setName(item.name());
        data.setCategory(item.category());
        data.setIsActive(item.isActive());
        data = categoryRepository.save(data);
        return toResponse(data);
    }

    @Override
    public void deleteData(String id) {
        CompanyCategory data = TreeGetEntity.parsingCompanyCategoryByProjection(id, categoryRepository);
        categoryRepository.delete(data);
    }

    private CompanyCategoryModel.CompanyCategoryDetailResponse toResponse(CompanyCategory data) {
        return new CompanyCategoryModel.CompanyCategoryDetailResponse(
                data.getName(),
                data.getCategory(),
                data.getIsActive()
        );
    }
}
