package com.kopibery.pos.service.impl;

import com.kopibery.pos.entity.Users;
import com.kopibery.pos.model.UserModel;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.UserService;
import com.kopibery.pos.util.GlobalConverter;
import com.kopibery.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ResultPageResponseDTO<UserModel.IndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);
        Page<Users> firstResult = userRepository.findDataByKeyword(set.keyword(), set.pageable());
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);

        Page<Users> pageResult = userRepository.findDataByKeyword(set.keyword(), pageable);
        List<UserModel.IndexResponse> dtos = pageResult.stream().map((c) -> {
            UserModel.IndexResponse dto = new UserModel.IndexResponse();
            dto.setName(c.getName());
            dto.setEmail(c.getEmail());

            GlobalConverter.CmsIDTimeStampResponseAndId(dto, c, userRepository);
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(pageResult, dtos);
    }

    @Override
    public UserModel.DetailResponse findDataById(String id) {
        Users data = TreeGetEntity.parsingUserByProjection(id, userRepository);
        return new UserModel.DetailResponse(
                data.getName(),
                data.getEmail()
        );
    }

    @Override
    public void saveData(UserModel.CreateRequest item) {

    }

    @Override
    public void updateData(String id, UserModel.UpdateRequest item) {

    }

    @Override
    public void deleteData(String id) {

    }
}
