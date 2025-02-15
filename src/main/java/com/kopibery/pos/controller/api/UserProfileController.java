package com.kopibery.pos.controller.api;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list menu", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get list menu", null));
        }
    }
}
