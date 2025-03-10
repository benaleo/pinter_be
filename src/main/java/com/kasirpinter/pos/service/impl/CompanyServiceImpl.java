package com.kasirpinter.pos.service.impl;

import static com.kasirpinter.pos.util.RandomStringGenerator.generateRandomAlphabetString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.kasirpinter.pos.model.attribute.AttributeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.model.CompanyModel;
import com.kasirpinter.pos.model.projection.CompanyIndexProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.CompanyRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.CompanyService;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @Override
    public ResultPageResponseDTO<CompanyModel.CompanyIndexResponse> listIndex(Integer pages, Integer limit,
            String sortBy, String direction, String keyword, Boolean isParent) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<CompanyIndexProjection> firstResult = companyRepository.findDataByKeyword(set.keyword(), set.pageable(),
                isParent);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<CompanyIndexProjection> pageResult = companyRepository.findDataByKeyword(set.keyword(), pageable,
                isParent);

        // Map the data to the DTOs
        List<CompanyModel.CompanyIndexResponse> dtos = pageResult.stream().map((c) -> {
            CompanyModel.CompanyIndexResponse dto = new CompanyModel.CompanyIndexResponse();
            dto.setName(c.getName());
            dto.setAddress(c.getAddress());
            dto.setCity(c.getCity());
            dto.setPhone(c.getPhone());

            List<String> companyNames = companyRepository.findListByParentId(c.getId());
            dto.setCompanyNames(companyNames);

            GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, c.getId(), c.getCreatedAt(), c.getUpdatedAt(),
                    c.getCreatedBy(), c.getCreatedBy());
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    @Override
    public CompanyModel.CompanyDetailResponse findDataBySecureId(String id) {
        Company data = TreeGetEntity.parsingCompanyByProjection(id, companyRepository);

        return convertToDetailResponse(data);
    }

    @Override
    public CompanyModel.CompanyDetailResponse saveData(CompanyModel.CompanyCreateRequest item) {
        Company newData = new Company();
        newData.setName(StringUtils.capitalize(item.getName()));
        newData.setAddress(item.getAddress());
        newData.setCity(item.getCity());
        newData.setPhone(item.getPhone());
        newData.setCode(generateRandomAlphabetString(6));
        newData.setIsActive(true);
        Company savedData = companyRepository.save(newData);

        // random code

        List<Company> childs = new ArrayList<>();
        AtomicInteger indexChild = new AtomicInteger(1);
        for (CompanyModel.CompanyChildRequest dto : item.getCompanies()) {
            Company newCompany = new Company();
            newCompany.setName(dto.getName());
            newCompany.setAddress(dto.getAddress());
            newCompany.setPhone(dto.getPhone());
            newCompany.setCity(dto.getCity());
            newCompany.setCode(savedData.getCode() + "-" + indexChild.getAndIncrement());
            newCompany.setParent(savedData);
            childs.add(newCompany);
        }
        companyRepository.saveAll(childs);

        return convertToDetailResponse(savedData);
    }

    @Override
    @Transactional
    public CompanyModel.CompanyDetailResponse updateData(String id, CompanyModel.CompanyUpdateRequest item) {
        try {
            Company data = TreeGetEntity.parsingCompanyByProjection(id, companyRepository);
            data.setName(StringUtils.capitalize(item.getName()));
            data.setAddress(item.getAddress() != null ? item.getAddress() : data.getAddress());
            data.setCity(item.getCity() != null ? item.getCity() : data.getCity());
            data.setPhone(item.getPhone() != null ? item.getPhone() : data.getPhone());
            data.setIsActive(item.getIsActive() != null ? item.getIsActive() : data.getIsActive());
            Company savedData = companyRepository.save(data);

            for (CompanyModel.CompanyChildRequest dto : item.getCompanies()) {
                Company childCompany = companyRepository.findBySecureId(dto.getId()).orElseGet(
                        () -> {
                            Company newChild = new Company();

                            newChild.setName(dto.getName());
                            newChild.setAddress(dto.getAddress());
                            newChild.setPhone(dto.getPhone());
                            newChild.setCity(dto.getCity());
                            newChild.setCode(savedData.getCode() + "-"
                                    + companyRepository.findAllByParent(savedData).size() + 1);
                            newChild.setParent(savedData);
                            return newChild;
                        });
                childCompany.setName(StringUtils.capitalize(dto.getName()));
                childCompany.setAddress(dto.getAddress() != null ? dto.getAddress() : childCompany.getAddress());
                childCompany.setCity(dto.getCity() != null ? dto.getCity() : childCompany.getCity());
                childCompany.setPhone(dto.getPhone() != null ? dto.getPhone() : childCompany.getPhone());
                companyRepository.save(childCompany);
            }

            return convertToDetailResponse(savedData);
        } catch (Exception e) {
            log.error("Error update data company : {}", e.getMessage(), e);
            throw new RuntimeException("Ups, Error while update");
        }
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        Company data = TreeGetEntity.parsingCompanyByProjection(id, companyRepository);
        List<Company> subData = companyRepository.findAllByParent(data);

        for (Company c : subData) {
            boolean isExistsOnUser = userRepository.existsByCompany(c);
            if (isExistsOnUser) {
                throw new RuntimeException("Company has been used on user");
            }
        }
        companyRepository.deleteAllByParent(data); // delete all child();
        companyRepository.delete(data);
    }

    private CompanyModel.CompanyDetailResponse convertToDetailResponse(Company data) {
        List<Company> childCompanies = companyRepository.findAllByParent(data);
        List<CompanyModel.CompanyChildResponse> childResponses = childCompanies.stream().map(c -> {
            return new CompanyModel.CompanyChildResponse(
                    c.getSecureId(),
                    c.getName(),
                    c.getAddress(),
                    c.getCity(),
                    c.getPhone(),
                    c.getIsActive());
        }).collect(Collectors.toList());
        return new CompanyModel.CompanyDetailResponse(
                data.getName(),
                data.getAddress(),
                data.getCity(),
                data.getPhone(),
                data.getIsActive(),
                childResponses);
    }

    @Override
    public List<AttributeResponse<String>> getListInputForm(String companyId) {
        Company data = TreeGetEntity.parsingCompanyByProjection(companyId, companyRepository);
        List<Company> datas = companyRepository.findAllByParentAndIsActiveIsTrue(data);

        return datas.stream()
                .map(dto -> {
                    AttributeResponse<String> response = new AttributeResponse<>();
                    response.setId(dto.getSecureId());
                    response.setName(dto.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
