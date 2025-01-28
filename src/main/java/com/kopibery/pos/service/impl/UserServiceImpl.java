package com.kopibery.pos.service.impl;

import com.kopibery.pos.entity.Company;
import com.kopibery.pos.entity.Roles;
import com.kopibery.pos.entity.Users;
import com.kopibery.pos.model.UserModel;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.CompanyRepository;
import com.kopibery.pos.repository.RoleRepository;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.UserService;
import com.kopibery.pos.util.GlobalConverter;
import com.kopibery.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.Tree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final CompanyRepository companyRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public ResultPageResponseDTO<UserModel.IndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<Users> firstResult = userRepository.findDataByKeyword(set.keyword(), set.pageable());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<Users> pageResult = userRepository.findDataByKeyword(set.keyword(), pageable);

        // Map the data to the DTOs
        List<UserModel.IndexResponse> dtos = pageResult.stream().map((c) -> {
            UserModel.IndexResponse dto = new UserModel.IndexResponse();
            dto.setName(c.getName());
            dto.setEmail(c.getEmail());
            dto.setAvatar(baseUrl + "/cms/v1/am/user/" + c.getSecureId() + "/avatar");

            dto.setRoleName(c.getRole().getName());
            dto.setCompanyName(c.getCompany() != null ? c.getCompany().getName() : null);

            dto.setIsActive(c.getIsActive());

            GlobalConverter.CmsIDTimeStampResponseAndId(dto, c, userRepository);
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }


    @Override
    public UserModel.DetailResponse findDataById(String id) {
        Users data = TreeGetEntity.parsingUserByProjection(id, userRepository);
        return new UserModel.DetailResponse(
                data.getName(),
                data.getEmail(),
                baseUrl + "/cms/v1/am/user/" + data.getSecureId() + "/avatar",
                data.getAvatarName(),
                data.getRole().getSecureId(),
                data.getRole().getName(),
                data.getCompany() != null ? data.getCompany().getSecureId() : null,
                data.getCompany() != null ? data.getCompany().getName() : null,
                data.getIsActive()
        );
    }

    @Override
    public void saveData(UserModel.CreateRequest item) {
        Roles role = TreeGetEntity.parsingRoleByProjection(item.getRoleId(), roleRepository);

        Users newUser = new Users();
        newUser.setName(item.getName());
        newUser.setEmail(item.getEmail());
        newUser.setPassword(passwordEncoder.encode(item.getPassword()));
        newUser.setIsActive(item.getIsActive());

        Company company = TreeGetEntity.parsingCompanyByProjection(item.getCompanyId(), companyRepository);
        newUser.setCompany(company);

        if (role != null) {
            newUser.setRole(role);
        }
        userRepository.save(newUser);
    }

    @Override
    public void updateData(String id, UserModel.UpdateRequest item) {
        Roles role = TreeGetEntity.parsingRoleByProjection(item.getRoleId(), roleRepository);

        Users user = TreeGetEntity.parsingUserByProjection(id, userRepository);
        user.setName(item.getName() != null ? item.getName() : user.getName());
        user.setPassword(item.getPassword() != null ? passwordEncoder.encode(item.getPassword()) : user.getPassword());
        user.setIsActive(item.getIsActive() != null ? item.getIsActive() : user.getIsActive());

        Company company = TreeGetEntity.parsingCompanyByProjection(item.getCompanyId(), companyRepository);
        user.setCompany(company);

        if (role != null) {
            user.setRole(role);
        }
        userRepository.save(user);
    }

    @Override
    public void updateAvatar(String id, MultipartFile avatar) throws IOException {
        Users user = TreeGetEntity.parsingUserByProjection(id, userRepository);
        byte[] fileBytes = avatar.getBytes();
        user.setAvatar(fileBytes);
        user.setAvatarName(avatar.getOriginalFilename());
        userRepository.save(user);
    }

    @Override
    public void deleteData(String id) {
        Users user = TreeGetEntity.parsingUserByProjection(id, userRepository);
        userRepository.delete(user);
    }

}
