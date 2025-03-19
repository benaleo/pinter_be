package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.*;
import com.kasirpinter.pos.model.UserShiftModel;
import com.kasirpinter.pos.model.UserShiftModel.ShiftAssignedRequest;
import com.kasirpinter.pos.model.UserShiftModel.ShiftAssignedResponse;
import com.kasirpinter.pos.model.projection.CastStringAndStringProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.*;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.UserShiftService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.Formatter;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserShiftServiceImpl implements UserShiftService {

    private final UserRepository userRepository;
    private final UserShiftRepository userShiftRepository;
    private final RlUserShiftRepository relationUserShiftRepository;
    private final MsJobPositionRepository msJobPositionRepository;

    private final CompanyRepository companyRepository;

    @Override
    public ResultPageResponseDTO<UserShiftModel.ShiftIndexResponse> listIndex(Integer pages, Integer limit,
                                                                              String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        String companyId = user.getRole().getName().equals("SUPERADMIN") ? null : user.getCompany().getSecureId();

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<MsShift> firstResult = userShiftRepository.findByNameLikeIgnoreCase(set.keyword(), set.pageable(),
                companyId);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<MsShift> pageResult = userShiftRepository.findByNameLikeIgnoreCase(set.keyword(), pageable, companyId);

        // Map the data to the DTOs
        List<UserShiftModel.ShiftIndexResponse> dtos = pageResult.stream().map((c) -> {
            UserShiftModel.ShiftIndexResponse dto = new UserShiftModel.ShiftIndexResponse();
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            dto.setPeriod(new UserShiftModel.PeriodStartEnd(c.getStartTime().toString(), c.getEndTime().toString()));
            dto.setCompany_name(c.getCompany() != null ? c.getCompany().getName() : null);
            dto.setIsActive(c.getIsActive());

            GlobalConverter.CmsIDTimeStampResponseAndId(dto, c, userRepository);
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    @Override
    public UserShiftModel.ShiftDetailResponse findDataBySecureId(String id) {
        MsShift shift = TreeGetEntity.parsingUserShiftByProjection(id, userShiftRepository);

        return ConvertToDetailResponse(shift);
    }

    @Override
    public UserShiftModel.ShiftDetailResponse saveData(UserShiftModel.ShiftCreateRequest dto) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        Company company = companyRepository.findBySecureId(dto.getCompanyId()).orElse(null);

        MsShift newData = new MsShift();
        newData.setName(dto.getName());
        newData.setDescription(dto.getDescription());
        newData.setStartTime(Formatter.parseToLocalTime(dto.getStart()));
        newData.setEndTime(Formatter.parseToLocalTime(dto.getEnd()));
        newData.setCompany(company != null ? company : user.getCompany());
        newData.setIsActive(true);

        GlobalConverter.CmsAdminCreateAtBy(newData, user.getId());
        MsShift savedData = userShiftRepository.save(newData);
        return ConvertToDetailResponse(savedData);
    }

    @Override
    @Transactional
    public UserShiftModel.ShiftDetailResponse updateData(String id, UserShiftModel.ShiftUpdateRequest dto) {
        MsShift data = TreeGetEntity.parsingUserShiftByProjection(id, userShiftRepository);
        data.setName(dto.getName() != null ? dto.getName() : data.getName());
        data.setDescription(dto.getDescription() != null ? dto.getDescription() : data.getDescription());
        data.setStartTime(dto.getStart() != null ? Formatter.parseToLocalTime(dto.getStart()) : data.getStartTime());
        data.setEndTime(dto.getEnd() != null ? Formatter.parseToLocalTime(dto.getEnd()) : data.getEndTime());
        data.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : data.getIsActive());

        GlobalConverter.CmsAdminUpdateAtBy(data, ContextPrincipal.getId());
        MsShift savedData = userShiftRepository.save(data);

        return ConvertToDetailResponse(savedData);
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        MsShift data = TreeGetEntity.parsingUserShiftByProjection(id, userShiftRepository);
        boolean exists = relationUserShiftRepository.existsByShift(data);
        if (exists) {
            log.info("updated shift to soft delete");
            userShiftRepository.updateByShift(data);
        } else {
            log.info("deleted shift to hard delete");
            userShiftRepository.delete(data);
        }

    }

    private UserShiftModel.ShiftDetailResponse ConvertToDetailResponse(MsShift data) {
        return new UserShiftModel.ShiftDetailResponse(
                data.getName(),
                data.getDescription(),
                new UserShiftModel.PeriodStartEnd(data.getStartTime().toString(), data.getEndTime().toString()));
    }

    // assigned user in shift
    @Override
    public ResultPageResponseDTO<ShiftAssignedResponse> listIndexAssigned(Integer pages, Integer limit, String sortBy,
                                                                          String direction, String keyword, String shiftId) {

        MsShift shift = TreeGetEntity.parsingUserShiftByProjection(shiftId, userShiftRepository);

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<RlUserShift> firstResult = relationUserShiftRepository.findByUserNameLikeIgnoreCaseAndShift(set.keyword(),
                set.pageable(), shift);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<RlUserShift> pageResult = relationUserShiftRepository.findByUserNameLikeIgnoreCaseAndShift(set.keyword(),
                pageable, shift);

        // Map the data to the DTOs
        List<UserShiftModel.ShiftAssignedResponse> dtos = pageResult.stream().map((c) -> {
            UserShiftModel.ShiftAssignedResponse dto = new UserShiftModel.ShiftAssignedResponse(
                    c.getUser() != null ? c.getUser().getSecureId() : null,
                    c.getUser() != null ? c.getUser().getName() : null,
                    c.getPosition() != null ? c.getPosition().getName() : null,
                    c.getCreatedAt() != null ? c.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                            : null,
                    c.getTsIn() != null ? c.getTsIn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : null,
                    c.getTsOut() != null ? c.getTsOut().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : null);
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    @Override
    public void saveDataAssigned(ShiftAssignedRequest item, String shiftId) {
        Users user = TreeGetEntity.parsingUserByProjection(item.userId(), userRepository);
        MsShift shift = TreeGetEntity.parsingUserShiftByProjection(shiftId, userShiftRepository);
        MsJobPosition position = item.positionId() != null ? TreeGetEntity.parseMsJobPositionByProjection(item.positionId(), msJobPositionRepository) : null;

        RlUserShift newData = new RlUserShift();
        newData.setUser(user);
        newData.setShift(shift);
        newData.setPosition(position);

        GlobalConverter.CmsAdminCreateAtBy(newData, ContextPrincipal.getId());
        relationUserShiftRepository.save(newData);
    }

    @Override
    public void deleteDataAssigned(String shiftId, String userId) {
        Users user = TreeGetEntity.parsingUserByProjection(userId, userRepository);
        MsShift shift = TreeGetEntity.parsingUserShiftByProjection(shiftId, userShiftRepository);
        RlUserShift data = relationUserShiftRepository.findByUserAndShift(user, shift);
        relationUserShiftRepository.delete(data);
    }

    @Override
    public ResultPageResponseDTO<ShiftAssignedResponse> listIndexNotAssign(Integer pages, Integer limit, String sortBy,
                                                                           String direction, String keyword, String shiftId) {
        MsShift shift = TreeGetEntity.parsingUserShiftByProjection(shiftId, userShiftRepository);
        Company company = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository).getCompany();
        log.info("assigned user shift: {}", shift.getUserShifts());

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<CastStringAndStringProjection> firstResult = userRepository.findAllUnassigedShift( set.keyword(), set.pageable(), company.getSecureId());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<CastStringAndStringProjection> pageResult = userRepository.findAllUnassigedShift( set.keyword(), pageable, company.getSecureId());

        // Map the data to the DTOs
        List<CastStringAndStringProjection> dtos = pageResult.stream().map((c) -> {
            return new CastStringAndStringProjection(
                    c.getId(),
                    c.getName());
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }
}
