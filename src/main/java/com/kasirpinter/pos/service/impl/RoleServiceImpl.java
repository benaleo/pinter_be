package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.converter.RoleDTOConverter;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.exception.BadRequestException;
import com.kasirpinter.pos.model.RoleModel;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.RolePermissionRepository;
import com.kasirpinter.pos.repository.RoleRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.RoleService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    private final RoleDTOConverter converter;

    @Override
    public ResultPageResponseDTO<RoleModel.IndexResponse> listData(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        // on pageable
        SavedKeywordAndPageable set = GlobalConverter.createPageable(pages, limit, sortBy, direction, keyword);
        Page<Roles> firstResult = roleRepository.findByNameLikeIgnoreCase(set.keyword(), set.pageable());
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        // on result
        Page<Roles> pageResult = roleRepository.findByNameLikeIgnoreCase(set.keyword(), pageable);
        List<RoleModel.IndexResponse> dtos = pageResult.stream().map(converter::convertToListResponse).collect(Collectors.toList());

        return PageCreateReturn.create(pageResult, dtos);
    }

    @Override
    public RoleModel.DetailResponse findDataBySecureId(String id) {
        Roles data = TreeGetEntity.parsingRoleByProjection(id, roleRepository);

        return converter.convertToDetailResponse(data);
    }

    @Override
    public RoleModel.DetailResponse saveData(RoleModel.CreateUpdateRequest item) {
        Long userId = ContextPrincipal.getId();

        // set entity to add with model mapper
        Roles data = converter.convertToCreateRequest(item, userId);
        // save data
        Roles savedData = roleRepository.save(data);
        // return parse
        return converter.convertToDetailResponse(savedData);
    }

    @Override
    public RoleModel.DetailResponse updateData(String id, RoleModel.CreateUpdateRequest item) {
        // Check if the role exists and get it
        Roles role = TreeGetEntity.parsingRoleByProjection(id, roleRepository);
        // convert
        converter.convertToUpdateRequest(role, item, ContextPrincipal.getId());
        // save update
        Roles savedData = roleRepository.save(role);
        // return parse
        return converter.convertToDetailResponse(savedData);
    }

    @Override
    public void deleteData(String id) {
        Roles role = TreeGetEntity.parsingRoleByProjection(id, roleRepository);
        Long roleId = role.getId();

        // Check for protected roles
        if (roleId >= 1 && roleId <= 5) {
            throw new BadRequestException("Role " + role.getName() + " cannot be deleted.");
        }

        // Proceed to delete associated permissions if needed
        // Assuming permissions should be removed, otherwise skip this part
        // Here you can decide whether to delete or keep permissions
        // If deleting:
        role.getListPermissions().forEach(rolePermissionRepository::delete);

        // Finally, delete the role
        if (roleRepository.existsById(roleId)) {
            roleRepository.deleteById(roleId);
        } else {
            throw new BadRequestException("Role not found");
        }
    }
}
