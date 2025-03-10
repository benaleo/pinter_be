package com.kasirpinter.pos.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.projection.CastKeyValueProjection;
import com.kasirpinter.pos.repository.CompanyRepository;
import com.kasirpinter.pos.repository.ProductCategoryRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.service.InputAttributeService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.TreeGetEntity;

import com.kasirpinter.pos.enums.ProductCategoryTypeInput;
import com.kasirpinter.pos.enums.ProductCategoryType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InputAttributeServiceImpl implements InputAttributeService {

    private final UserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CompanyRepository companyRepository;

    @Override
    public List<Map<String, String>> getListCompany() {
        String roleName = ContextPrincipal.getRoleName();
        boolean isAll = roleName.equals("SUPERADMIN");
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        String companyId = isAll || user.getCompany() == null ? null : user.getCompany().getSecureId();
        List<CastKeyValueProjection> data = companyRepository.getListInputForm(companyId);
        return data.stream().map((c) -> {
            return Map.of("id", c.getKey(), "name", c.getValue());
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getListProductCategory(ProductCategoryTypeInput type) {

        ProductCategoryType typeEnum = type.equals(ProductCategoryTypeInput.ALL) ? null : ProductCategoryType.valueOf(type.name());

        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        String companyId = user.getCompany() == null ? null : user.getCompany().getSecureId();
        List<CastKeyValueProjection> data = productCategoryRepository.getListInputForm(companyId, typeEnum);
        return data.stream().map((c) -> {
            return Map.of("id", c.getKey(), "name", c.getValue());
        }).collect(Collectors.toList());
    }
}