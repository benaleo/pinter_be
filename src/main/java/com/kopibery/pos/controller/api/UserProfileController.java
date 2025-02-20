package com.kopibery.pos.controller.api;

import com.kopibery.pos.enums.InOutType;
import com.kopibery.pos.exception.BadRequestException;
import com.kopibery.pos.model.UserModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(UserProfileController.urlRoute)
@Tag(name = "User API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class UserProfileController {

    static final String urlRoute = "/api/v1/user";

    private final UserService userService;

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
        } catch (BadRequestException e){
            log.error("Error edit company modal : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error edit company modal : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error set modal", null));
        }
    }





}
