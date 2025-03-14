package com.kasirpinter.pos.controller.api;

import com.kasirpinter.pos.enums.InOutType;
import com.kasirpinter.pos.exception.BadRequestException;
import com.kasirpinter.pos.model.UserModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping(UserProfileController.urlRoute)
@Tag(name = "User API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class UserProfileController {

    static final String urlRoute = "/api/v1/user";

    private final UserService userService;

    // check user auth
    @Operation(summary = "Get user info", description = "Get user info")
    @GetMapping("/check")
    public ResponseEntity<?> getUserCheck() {
        // response true
        log.info("GET " + urlRoute + "/info endpoint hit");
        try {
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success check user auth", null));
        } catch (BadRequestException e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get user info", null));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Unauthorized", null));
        }
    }

    // category menu
    @Operation(summary = "Get user info", description = "Get user info")
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        // response true
        log.info("GET " + urlRoute + "/info endpoint hit");
        try {
            UserModel.UserInfo response = userService.getUserInfo();
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get user info", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get user info", null));
        }
    }

    // presence user in
    @Operation(summary = "Get user info", description = "Get user info")
    @PutMapping("/presence")
    public ResponseEntity<?> getPresenceUser(@RequestParam InOutType type) {
        // response true
        log.info("GET " + urlRoute + "/presence endpoint hit");
        try {
            UserModel.UserInfo response = userService.getPresenceUserIn(type);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get user info", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get user info", null));
        }
    }

    // Edit company modal
    @Operation(summary = "Edit company modal", description = "Edit company modal")
    @PutMapping("/company/modal")
    public ResponseEntity<?> setCompanyModal(@RequestParam Integer value) {
        // response true
        log.info("GET " + urlRoute + "/company/modal endpoint hit");
        try {
            UserModel.UserInfo response = userService.setCompanyModal(value);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success edit company modal", response));
        } catch (BadRequestException e) {
            log.error("Error edit company modal : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error edit company modal : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error set modal", null));
        }
    }

    // Edit user profile
    @Operation(summary = "Edit profile", description = "Edit profile")
    @PutMapping("/profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody UserModel.userUpdateAppRequest req) {
        // response true
        log.info("GET " + urlRoute + "/profile endpoint hit");
        try {
            UserModel.UserInfo response = userService.updateMyProfile(req);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success edit my profile", response));
        } catch (BadRequestException e) {
            log.error("Error edit profile : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error edit profile : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed update profile", null));
        }
    }

    // Edit user avatar
    @Operation(summary = "Edit profile", description = "Edit profile")
    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyProfileAvatar(@RequestPart(required = false) MultipartFile avatar, @RequestParam(required = false, defaultValue = "false") Boolean isRemove) {
        // response true
        log.info("GET " + urlRoute + "/avatar endpoint hit");
        try {
            UserModel.UserInfo response = userService.updateMyProfileAvatar(avatar, isRemove);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success edit my profile avatar", response));
        } catch (BadRequestException e) {
            log.error("Error edit profile avatar : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error edit profile avatar : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed update profile avatar", null));
        }
    }

    // Edit user cover
    @Operation(summary = "Edit cover", description = "Edit cover")
    @PutMapping(value = "/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyProfileCover(@RequestPart(required = false) MultipartFile cover, @RequestParam(required = false, defaultValue = "false") Boolean isRemove) {
        // response true
        log.info("GET " + urlRoute + "/cover endpoint hit");
        try {
            UserModel.UserInfo response = userService.updateMyProfileCover(cover, isRemove);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success edit my profile cover", response));
        } catch (BadRequestException e) {
            log.error("Error edit profile cover : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error edit profile cover : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed update profile cover", null));
        }
    }

    // Edit user password
    @Operation(summary = "Edit profile", description = "Edit profile")
    @PutMapping("/password")
    public ResponseEntity<?> updateMyProfile(@RequestBody UserModel.userUpdatePasswordRequest req) {
        // response true
        log.info("GET " + urlRoute + "/password endpoint hit");
        try {
            userService.updateMyPassword(req);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success edit my profile", null));
        } catch (BadRequestException e) {
            log.error("Error edit profile : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error edit profile : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed update profile", null));
        }
    }

}
