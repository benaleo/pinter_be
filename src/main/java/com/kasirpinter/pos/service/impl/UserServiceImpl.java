package com.kasirpinter.pos.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.kasirpinter.pos.converter.LogGeneralConverter;
import com.kasirpinter.pos.model.LogGeneralRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kasirpinter.pos.converter.TreeRolePermissionConverter;
import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.MsShift;
import com.kasirpinter.pos.entity.Permissions;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.RolePermission;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.entity.ShiftRecap;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.enums.InOutType;
import com.kasirpinter.pos.exception.BadRequestException;
import com.kasirpinter.pos.model.RolePermissionModel;
import com.kasirpinter.pos.model.UserModel;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.CompanyRepository;
import com.kasirpinter.pos.repository.PermissionsRepository;
import com.kasirpinter.pos.repository.RlUserShiftRepository;
import com.kasirpinter.pos.repository.RolePermissionRepository;
import com.kasirpinter.pos.repository.RoleRepository;
import com.kasirpinter.pos.repository.ShiftRecapRepository;
import com.kasirpinter.pos.repository.TransactionRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.repository.UserShiftRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.UserService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionsRepository permissionRepository;
    private final RlUserShiftRepository relationUserShiftRepository;
    private final ShiftRecapRepository shiftRecapRepository;
    private final UserShiftRepository userShiftRepository;

    private final TransactionRepository transactionRepository;
    private final CompanyRepository companyRepository;

    private final PasswordEncoder passwordEncoder;

    private final LogGeneralConverter logConverter;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public ResultPageResponseDTO<UserModel.userIndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy,
            String direction, String keyword) {
        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

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
                dtos);
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
                data.getIsActive());
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
        user.setPassword(item.getPassword() != null && item.getPassword().length() > 0  ? passwordEncoder.encode(item.getPassword()) : user.getPassword());
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
        // Users user =
        // TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(),
        // userRepository);
        //
        // RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user,
        // LocalDate.now()).orElse(null);

        return parseUserInfo(null);
    }

    @Override
    @Transactional
    public UserModel.UserInfo getPresenceUserIn(InOutType type) {
        LocalDateTime now = LocalDateTime.now();
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createRlUserShiftOnNull(user));
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
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createRlUserShiftOnNull(user));
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
    public UserModel.UserInfo updateMyProfile(UserModel.userUpdateAppRequest req) {
        Map<String, Object> changedData = new HashMap<>();
        Map<String, Object> beforeData = new HashMap<>();

        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        user.setName(req.name() != null ? req.name() : user.getName());
        user.setEmail(req.email() != null ? req.email() : user.getEmail());
        user.setPhone(req.phone() != null ? req.phone() : user.getPhone());
        userRepository.save(user);

        if (req.name() != null && !req.name().equals(user.getName())) {
            changedData.put("name", req.name());
            beforeData.put("name", Optional.ofNullable(user.getName()).orElse(""));
        }

        if (req.email() != null && !req.email().equals(user.getEmail())) {
            changedData.put("email", req.email());
            beforeData.put("email", Optional.ofNullable(user.getEmail()).orElse(""));
        }

        if (req.phone() != null && !req.phone().equals(user.getPhone())) {
            changedData.put("phone", req.phone());
            beforeData.put("phone", Optional.ofNullable(user.getPhone()).orElse(""));
        }

        List<Map<String, String>> listResponse = new ArrayList<>();
        for (Map.Entry<String, Object> entry : changedData.entrySet()) {
            String key = entry.getKey();
            String valueBefore = beforeData.get(key) == null ? "" : beforeData.get(key).toString();
            String valueAfter = changedData.get(key) == null ? "" : changedData.get(key).toString();
            Map<String, String> response = new HashMap<>();
            response.put("property", key);
            response.put("before", valueBefore);
            response.put("after", valueAfter);
            listResponse.add(response);
        }

        String jsonResponse = new Gson().toJson(listResponse).replaceAll("\\\\", "");
        // save to log
        LogGeneralRequest log = new LogGeneralRequest(
                user.getSecureId(),
                "USER_ACTIVE",
                jsonResponse,
                "ACTIVE",
                "UPDATED",
                "ADMIN");
        logConverter.sendLogHistory(log);

        return parseUserInfo(null);
    }

    @Override
    public UserModel.UserInfo updateMyProfileAvatar(MultipartFile avatar) throws IOException {
        String fileName = avatar.getOriginalFilename();
        String fileExtension = fileName != null ? fileName.substring(fileName.lastIndexOf(".") + 1 ): null;

        if (!Arrays.asList("jpg", "png", "jpeg").contains(fileExtension)) {
            throw new BadRequestException("Only allowed .jpg / .png / .jpeg");
        }

        byte[] fileBytes = avatar.getBytes();

        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        user.setAvatar(fileBytes);
        user.setAvatarName(fileName);
        userRepository.save(user);

        return parseUserInfo(null);
    }

    @Override
    public void updateMyPassword(UserModel.userUpdatePasswordRequest req) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Password lama salah");
        }
        if (!req.password().equals(req.confirmPassword())) {
            throw new BadRequestException("Password baru tidak sama");
        }
        user.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(user);
    }

    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));
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
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createRlUserShiftOnNull(user));
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
                        .collect(Collectors.toList()));
    }

    private RlUserShift createRlUserShiftOnNull(Users user) {
        RlUserShift newShift = new RlUserShift();
        newShift.setUser(user);
        newShift.setDate(LocalDate.now());
        return relationUserShiftRepository.save(newShift);
    }

    @Override
    public UserModel.AdminInfo getAdminInfo() {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDate(user, LocalDate.now())
                .orElseGet(() -> createRlUserShiftOnNull(user));
        ShiftRecap recap = shiftRecapRepository.findByShift(userShift.getShift());
        InOutType type = null;

        Roles role = user.getRole();
        List<RolePermission> roleHasPermissionList = role.getListPermissions();
        TreeRolePermissionConverter converter = new TreeRolePermissionConverter();
        List<RolePermissionModel.ListPermission> menuNames = converter.convertRolePermissions(roleHasPermissionList,
                permissionRepository, "info");

        return new UserModel.AdminInfo(
                user.getSecureId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getCompany() != null ? user.getCompany().getSecureId() : null,
                user.getCompany() != null ? user.getCompany().getName() : null,
                recap != null ? recap.getCash() : 0,
                type != null && type.equals(InOutType.IN) ? user.getNow() : user.userClockIn(),
                type != null && type.equals(InOutType.OUT) ? user.getNow() : user.userClockOut(),
                menuNames);
    }
}
