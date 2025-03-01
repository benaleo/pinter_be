package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.*;
import com.kasirpinter.pos.enums.InOutType;
import com.kasirpinter.pos.exception.BadRequestException;
import com.kasirpinter.pos.model.UserModel;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.*;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.UserService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RlUserShiftRepository relationUserShiftRepository;
    private final ShiftRecapRepository shiftRecapRepository;
    private final UserShiftRepository userShiftRepository;

    private final TransactionRepository transactionRepository;
    private final CompanyRepository companyRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public ResultPageResponseDTO<UserModel.userIndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<Users> firstResult = userRepository.findDataByKeyword(set.keyword(), set.pageable());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<Users> pageResult = userRepository.findDataByKeyword(set.keyword(), pageable);

        // Map the data to the DTOs
        List<UserModel.userIndexResponse> dtos = pageResult.stream().map((c) -> {
            UserModel.userIndexResponse dto = new UserModel.userIndexResponse();
            dto.setName(c.getName());
            dto.setEmail(c.getEmail());
            dto.setAvatar(c.getAvatar() != null ? baseUrl + "/cms/v1/am/user/" + c.getSecureId() + "/avatar" : null);

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
    public UserModel.userDetailResponse findDataById(String id) {
        Users data = TreeGetEntity.parsingUserByProjection(id, userRepository);
        return new UserModel.userDetailResponse(
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
    public void saveData(UserModel.userCreateRequest item) {
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
    public void updateData(String id, UserModel.userUpdateRequest item) {
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

    @Override
    @Transactional
    public UserModel.UserInfo getUserInfo() {
//        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
//
//        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now()).orElse(null);

        return parseUserInfo(null);
    }

    @Override
    @Transactional
    public UserModel.UserInfo getPresenceUserIn(InOutType type) {
        LocalDateTime now = LocalDateTime.now();
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now()).orElseGet(() -> createRlUserShiftOnNull(user));
        if (type.equals(InOutType.IN)) {
            relationUserShiftRepository.updateTsByUserShiftId(userShift.getId(), now, true);
        }
        if (type.equals(InOutType.OUT)) {
            relationUserShiftRepository.updateTsByUserShiftId(userShift.getId(), now, false);
        }

        return parseUserInfo(type);
    }

    @Override
    @Transactional
    public UserModel.UserInfo setCompanyModal(Integer value) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now()).orElseGet(() -> createRlUserShiftOnNull(user));
        if (userShift.getShift() == null) {
            throw new BadRequestException("Mohon untuk masukan ke shift terlebih dahulu.");
        }
        ShiftRecap recap = Optional.of(shiftRecapRepository.findByShift(userShift.getShift())).orElse(null);
        if (recap.getCash() == null) {
            recap.setCash(value);
        } else {
            if (transactionRepository.existsByUserShift(userShift)) {
                throw new BadRequestException("Tidak bisa melakukan aksi karena sudah ada transaksi.");
            } else {
                recap.setCash(value);
            }
        }
        return parseUserInfo(null);
    }

    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email)
        );
    }

    @Override
    @Transactional
    public void assignUserToShift(UserModel.userAssignShiftRequest item) {
        List<RlUserShift> relationShifts = userRepository.findBySecureIdIn(item.getUserIds()).stream()
                .map(user -> {
                    MsShift shift = TreeGetEntity.parsingUserShiftByProjection(item.getShiftId(), userShiftRepository);
                    RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now())
                            .orElseGet(() -> {
                                RlUserShift newShift = new RlUserShift();
                                newShift.setShift(shift);
                                newShift.setUser(user);
                                newShift.setDate(LocalDate.now());
                                newShift.setCreatedBy(ContextPrincipal.getId());
                                return newShift;
                            });
                    userShift.setShift(shift);
                    userShift.setUpdatedAt(LocalDateTime.now());
                    userShift.setUpdatedBy(ContextPrincipal.getId());
                    return userShift;
                })
                .collect(Collectors.toList());
        if (!relationShifts.isEmpty()) {
            log.info("run save entity");
            relationUserShiftRepository.saveAll(relationShifts);
        }
    }

    private UserModel.UserInfo parseUserInfo(InOutType type) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now()).orElseGet(() -> createRlUserShiftOnNull(user));
        ShiftRecap recap = shiftRecapRepository.findByShift(userShift.getShift());
        return new UserModel.UserInfo(
                user.getSecureId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getCompany() != null ? user.getCompany().getSecureId() : null,
                user.getCompany() != null ? user.getCompany().getName() : null,
                recap != null ? recap.getCash() : 0,
                type != null && type.equals(InOutType.IN) ? user.getNow() : user.userClockIn(),
                type != null && type.equals(InOutType.OUT) ? user.getNow() : user.userClockOut(),
                rolePermissionRepository.findByRole(user.getRole()).stream()
                        .map(RolePermission::getPermission)
                        .map(Permissions::getName)
                        .map(permission -> Map.of("name", permission))
                        .collect(Collectors.toList())
        );
    }

    private RlUserShift createRlUserShiftOnNull(Users user) {
        RlUserShift newShift = new RlUserShift();
        newShift.setUser(user);
        newShift.setDate(LocalDate.now());
        return relationUserShiftRepository.save(newShift);
    }
}
