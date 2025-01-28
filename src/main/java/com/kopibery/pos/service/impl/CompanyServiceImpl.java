package com.kopibery.pos.service.impl;

import com.kopibery.pos.entity.Company;
import com.kopibery.pos.model.CompanyModel;
import com.kopibery.pos.model.projection.CompanyIndexProjection;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.CompanyRepository;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.CompanyService;
import com.kopibery.pos.util.GlobalConverter;
import com.kopibery.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @Override
    public ResultPageResponseDTO<CompanyModel.IndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword, Boolean isParent) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<CompanyIndexProjection> firstResult = companyRepository.findDataByKeyword(set.keyword(), set.pageable(), isParent);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<CompanyIndexProjection> pageResult = companyRepository.findDataByKeyword(set.keyword(), pageable, isParent);

        // Map the data to the DTOs
        List<CompanyModel.IndexResponse> dtos = pageResult.stream().map((c) -> {
            CompanyModel.IndexResponse dto = new CompanyModel.IndexResponse();
            dto.setName(c.getName());
            dto.setAddress(c.getAddress());
            dto.setCity(c.getCity());
            dto.setPhone(c.getPhone());

            List<String> companyNames = companyRepository.findAllByParentId(c.getId());
            dto.setCompanyNames(companyNames);

            GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, c.getId(), c.getCreatedAt(), c.getUpdatedAt(), c.getCreatedBy(), c.getCreatedBy());
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public CompanyModel.DetailResponse findDataBySecureId(String id) {
        Company data = TreeGetEntity.parsingCompanyByProjection(id, companyRepository);

        return convertToDetailResponse(data);
    }

    @Override
    public CompanyModel.DetailResponse saveData(CompanyModel.CreateRequest item) {
        Company newData = new Company();
        newData.setName(StringUtils.capitalize(item.getName()));
        newData.setAddress(item.getAddress());
        newData.setCity(item.getCity());
        newData.setPhone(item.getPhone());
        Company savedData = companyRepository.save(newData);

        for (String subCompanyName : item.getCompanyNames()){
            Company company = companyRepository.findByName(subCompanyName)
                    .orElseGet(() -> {
                        Company newCompany = new Company();
                        newCompany.setName(subCompanyName);
                        newCompany.setParent(savedData);
                        return companyRepository.save(newCompany);
                    });

            company.setParent(savedData);
            companyRepository.save(company);
        }

        return convertToDetailResponse(savedData);
    }

    @Override
    public CompanyModel.DetailResponse updateData(String id, CompanyModel.UpdateRequest item) {
        Company data = TreeGetEntity.parsingCompanyByProjection(id, companyRepository);
        data.setName(StringUtils.capitalize(item.getName()));
        data.setAddress(item.getAddress() != null ? item.getAddress() : data.getAddress());
        data.setCity(item.getCity() != null ? item.getCity() : data.getCity());
        data.setPhone(item.getPhone() != null ? item.getPhone() : data.getPhone());
        Company savedData = companyRepository.save(data);

        return convertToDetailResponse(savedData);
    }

    @Override
    public void deleteData(String id) {
        Company data = TreeGetEntity.parsingCompanyByProjection(id, companyRepository);
        companyRepository.delete(data);
    }

    private CompanyModel.DetailResponse convertToDetailResponse(Company data) {
        List<String> companyNames = companyRepository.findAllByParentId(data.getSecureId());
        return new CompanyModel.DetailResponse(
                data.getName(),
                data.getAddress(),
                data.getCity(),
                data.getPhone(),
                companyNames
        );
    }
}
