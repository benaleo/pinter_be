package com.kopibery.pos.service.impl;

import com.kopibery.pos.entity.MsShift;
import com.kopibery.pos.entity.Users;
import com.kopibery.pos.model.UserShiftModel;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.RlUserShiftRepository;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.repository.UserShiftRepository;
import com.kopibery.pos.response.PageCreateReturn;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.UserShiftService;
import com.kopibery.pos.util.ContextPrincipal;
import com.kopibery.pos.util.GlobalConverter;
import com.kopibery.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.Tree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserShiftServiceImpl implements UserShiftService {

    private final UserRepository userRepository;
    private final UserShiftRepository userShiftRepository;
    private final RlUserShiftRepository relationUserShiftRepository;

    @Override
    public ResultPageResponseDTO<UserShiftModel.ShiftIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<MsShift> firstResult = userShiftRepository.findByNameContainingIgnoreCase(set.keyword(), set.pageable());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<MsShift> pageResult = userShiftRepository.findByNameContainingIgnoreCase(set.keyword(), pageable);

        // Map the data to the DTOs
        List<UserShiftModel.ShiftIndexResponse> dtos = pageResult.stream().map((c) -> {
            UserShiftModel.ShiftIndexResponse dto = new UserShiftModel.ShiftIndexResponse();
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            dto.setStart(c.getStartTime().toString());
            dto.setEnd(c.getEndTime().toString());
            dto.setCompany_name(user.getCompany() != null ? user.getCompany().getName() : null);

            GlobalConverter.CmsIDTimeStampResponseAndId(dto, c, userRepository);
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public UserShiftModel.ShiftDetailResponse findDataBySecureId(String id) {
        MsShift shift = TreeGetEntity.parsingUserShiftByProjection(id, userShiftRepository);

        return ConvertToDetailResponse(shift);
    }

    @Override
    public UserShiftModel.ShiftDetailResponse saveData(UserShiftModel.ShiftCreateRequest dto) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        MsShift newData = new MsShift();
        newData.setName(dto.getName());
        newData.setDescription(dto.getDescription());
        newData.setStartTime(dto.getStart());
        newData.setEndTime(dto.getEnd());
        newData.setCompany(user.getCompany());
        MsShift savedData = userShiftRepository.save(newData);
        return ConvertToDetailResponse(savedData);
    }

    @Override
    public UserShiftModel.ShiftDetailResponse updateData(String id, UserShiftModel.ShiftUpdateRequest dto) {
        MsShift data = TreeGetEntity.parsingUserShiftByProjection(id, userShiftRepository);
        data.setName(dto.getName() != null ? dto.getName() : data.getName());
        data.setDescription(dto.getDescription() != null ? dto.getDescription() : data.getDescription());
        data.setStartTime(dto.getStart() != null ? dto.getStart() : data.getStartTime());
        data.setEndTime(dto.getEnd() != null ? dto.getEnd() : data.getEndTime());
        MsShift savedData = userShiftRepository.save(data);

        return ConvertToDetailResponse(savedData);
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        MsShift data = TreeGetEntity.parsingUserShiftByProjection(id, userShiftRepository);
        boolean exists = relationUserShiftRepository.existsByShift(data);
        if (exists){
            log.info("updated shift to soft delete");
            userShiftRepository.updateByShift(data);
        } else {
            log.info("deleted shift to hard delete");
            userShiftRepository.delete(data);
        }

    }

    private UserShiftModel.ShiftDetailResponse ConvertToDetailResponse(MsShift data){
        return new UserShiftModel.ShiftDetailResponse(
                data.getName(),
                data.getDescription(),
                data.getStartTime().toString(),
                data.getEndTime().toString()
        );
    }
}
