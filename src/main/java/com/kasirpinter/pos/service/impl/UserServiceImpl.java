package com.kasirpinter.pos.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.kasirpinter.pos.converter.ImageUrlConverter;
import com.kasirpinter.pos.converter.LogGeneralConverter;
import com.kasirpinter.pos.entity.*;
import com.kasirpinter.pos.enums.FileEntity;
import com.kasirpinter.pos.enums.FileType;
import com.kasirpinter.pos.model.LogGeneralRequest;
import com.kasirpinter.pos.model.projection.StringProjection;
import com.kasirpinter.pos.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kasirpinter.pos.converter.TreeRolePermissionConverter;
import com.kasirpinter.pos.enums.InOutType;
import com.kasirpinter.pos.exception.BadRequestException;
import com.kasirpinter.pos.model.RolePermissionModel;
import com.kasirpinter.pos.model.UserModel;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
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
    private final ImageUrlConverter urlConverter;

    private final LogGeneralConverter logConverter;
    private final FileManagerRepository fileManagerRepository;

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
            FileManager fileAvatar = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, c.getId())).orElse(null);
            FileManager fileCover = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, c.getId())).orElse(null);

            dto.setName(c.getName());
            dto.setEmail(c.getEmail());
            dto.setAvatar(fileAvatar == null ? null : fileAvatar.getFileUrl());
            dto.setCover(fileCover == null ? null : fileCover.getFileUrl());

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
        FileManager fileAvatar = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, data.getId())).orElse(null);
        FileManager fileCover = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, data.getId())).orElse(null);
        return new UserModel.userDetailResponse(
                data.getName(),
                data.getEmail(),
                fileAvatar == null ? null : fileAvatar.getFileUrl(),
                fileAvatar == null ? null : fileAvatar.getFileName(),
                fileCover == null ? null : fileCover.getFileUrl(),
                fileCover == null ? null : fileCover.getFileName(),
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
        if (avatar != null && avatar.getOriginalFilename() != null){
            byte[] fileBytes = avatar.getBytes();
            String fileName = avatar.getOriginalFilename();
            FileManager fileManager = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, user.getId())).orElseGet(
                    () -> saveFile(user, fileBytes, fileName, "AVATAR")
            );
            fileManager.setFile(fileBytes);
            fileManager.setFileName(fileName);
            fileManager.setFileUrl(fileManager.getFile() != null ? urlConverter.getUserAvatar(user.getSecureId()) : null);
            fileManagerRepository.save(fileManager);
        }
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
            // save to log
            logConverter.sendHistoryPresence(user, "IN");
        }
        if (type.equals(InOutType.OUT)) {
            relationUserShiftRepository.updateTsByUserShiftId(userShift.getId(), now, false);
            // save to log
            logConverter.sendHistoryPresence(user, "OUT");
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
        // save to log
        logConverter.sendUpdateCompanyModal(user, userShift, value);
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
        user.setAddress(req.address() != null ? req.address() : user.getAddress());
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

        if (req.address() != null && !req.address().equals(user.getAddress())) {
            changedData.put("address", req.address());
            beforeData.put("address", Optional.ofNullable(user.getAddress()).orElse(""));
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
        logConverter.sendLogHistoryUpdateProfile(user, "PROFILE", "DATA", "UPDATED", jsonResponse);
        return parseUserInfo(null);
    }

    @Override
    @Transactional
    public UserModel.UserInfo updateMyProfileAvatar(MultipartFile avatar, Boolean isRemove) throws IOException {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        if (avatar != null && avatar.getOriginalFilename() != null){

            String fileName = avatar.getOriginalFilename();
            String fileExtension = fileName != null ? fileName.substring(fileName.lastIndexOf(".") + 1 ): null;

            if (!Arrays.asList("jpg", "png", "jpeg").contains(fileExtension)) {
                throw new BadRequestException("Only allowed .jpg / .png / .jpeg");
            }

            byte[] fileBytes = avatar.getBytes();

            FileManager fileManager = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, user.getId())).orElseGet(
                    () -> saveFile(user, fileBytes, fileName, "AVATAR")
            );
            fileManager.setFile(fileBytes);
            fileManager.setFileName(fileName);
            fileManager.setFileUrl(fileManager.getFile() != null ? urlConverter.getUserAvatar(user.getSecureId()) : null);
            fileManagerRepository.save(fileManager);
            // save to log
            logConverter.sendLogHistoryAvatar(user, "PROFILE", "COVER", "COVER-UPDATED");
        }

        if (isRemove){
            fileManagerRepository.deleteByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, user.getId());
            // save to log
            logConverter.sendLogHistoryAvatar(user, "PROFILE", "AVATAR", "AVATAR-REMOVED");
        }

        return parseUserInfo(null);
    }

    @Override
    @Transactional
    public UserModel.UserInfo updateMyProfileCover(MultipartFile cover, Boolean isRemove) throws IOException {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);

        if (cover != null && cover.getOriginalFilename() != null){

            String fileName = cover.getOriginalFilename();
            String fileExtension = fileName != null ? fileName.substring(fileName.lastIndexOf(".") + 1 ): null;

            if (!Arrays.asList("jpg", "png", "jpeg").contains(fileExtension)) {
                throw new BadRequestException("Only allowed .jpg / .png / .jpeg");
            }

            byte[] fileBytes = cover.getBytes();

            FileManager fileManager = Optional.ofNullable(fileManagerRepository.findByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, user.getId())).orElseGet(
                    () -> {
                        FileManager newFileManager = new FileManager(
                                fileBytes,
                                fileName,
                                FileEntity.USER,
                                FileType.IMAGE,
                                "COVER",
                                urlConverter.getUserCover(user.getSecureId()),
                                user.getId()
                        );
                        return fileManagerRepository.save(newFileManager);
                    }
            );
            fileManager.setFile(fileBytes);
            fileManager.setFileName(fileName);
            fileManager.setFileUrl(fileManager.getFile() != null ? urlConverter.getUserCover(user.getSecureId()) : null);
            fileManagerRepository.save(fileManager);
            // save to log
            logConverter.sendLogHistoryAvatar(user, "PROFILE", "COVER", "COVER-UPDATED");
        }

        if (isRemove){
            fileManagerRepository.deleteByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, user.getId());
            // save to log
            logConverter.sendLogHistoryAvatar(user, "PROFILE", "COVER", "COVER-REMOVED");
        }

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

        // save to log
        logConverter.sendLogHistoryPassword(user, "PROFILE");
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
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDateAndShiftIsNotNull(user, LocalDate.now())
                .orElseGet(() -> createRlUserShiftOnNull(user));
        ShiftRecap recap = shiftRecapRepository.findByShift(userShift.getShift());
        String avatar = fileManagerRepository.findFileUrlByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, user.getId());
        String cover = fileManagerRepository.findFileUrlByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, user.getId());
        return new UserModel.UserInfo(
                user.getSecureId(),
                avatar,
                cover,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
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

        List<MsShift> listShift = userShiftRepository.findAllByNowIsBetweenStartAndEnd(LocalTime.now());
        MsShift shift = null;
        if (!listShift.isEmpty()) {
            shift = listShift.getFirst();
        }

        RlUserShift newShift = new RlUserShift();
        newShift.setUser(user);
        newShift.setDate(LocalDate.now());
        newShift.setShift(shift);
        return relationUserShiftRepository.save(newShift);
    }

    @Override
    public UserModel.AdminInfo getAdminInfo() {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        RlUserShift userShift = relationUserShiftRepository.findByUserAndDateAndShiftIsNotNull(user, LocalDate.now())
                .orElseGet(() -> createRlUserShiftOnNull(user));
        ShiftRecap recap = shiftRecapRepository.findByShift(userShift.getShift());
        InOutType type = null;

        Roles role = user.getRole();
        List<RolePermission> roleHasPermissionList = role.getListPermissions();
        TreeRolePermissionConverter converter = new TreeRolePermissionConverter();
        List<RolePermissionModel.ListPermission> menuNames = converter.convertRolePermissions(roleHasPermissionList,
                permissionRepository, "info");

        String avatar = fileManagerRepository.findFileUrlByFileAsAndFileEntityAndEntityId("AVATAR", FileEntity.USER, user.getId());
        String cover = fileManagerRepository.findFileUrlByFileAsAndFileEntityAndEntityId("COVER", FileEntity.USER, user.getId());

        return new UserModel.AdminInfo(
                user.getSecureId(),
                user.getName(),
                user.getEmail(),
                avatar,
                cover,
                user.getPhone(),
                user.getAddress(),
                user.getRole().getName(),
                user.getCompany() != null ? user.getCompany().getSecureId() : null,
                user.getCompany() != null ? user.getCompany().getName() : null,
                recap != null ? recap.getCash() : 0,
                type != null && type.equals(InOutType.IN) ? user.getNow() : user.userClockIn(),
                type != null && type.equals(InOutType.OUT) ? user.getNow() : user.userClockOut(),
                menuNames);
    }

    private FileManager saveFile(Users user, byte[] fileBytes, String fileName, String fileAs) {
        FileManager newFileManager = new FileManager(
                fileBytes,
                fileName,
                FileEntity.USER,
                FileType.IMAGE,
                fileAs,
                urlConverter.getUserCover(user.getSecureId()),
                user.getId()
        );
        return fileManagerRepository.save(newFileManager);
    }
}
