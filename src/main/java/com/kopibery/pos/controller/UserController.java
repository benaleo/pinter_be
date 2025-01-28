package com.kopibery.pos.controller;

import com.kopibery.pos.model.UserModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping(UserController.urlRoute)
@Tag(name = "Users API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class UserController {

    static final String urlRoute = "/cms/v1/am/user";
    private UserService service;

    @Operation(summary = "GET List User", description = "API for get list user index with pagination")
    @GetMapping
    public ResponseEntity<?> listDataUserIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "name") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");

        try {
            ResultPageResponseDTO<UserModel.IndexResponse> response = service.findDataIndex(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list user", response));
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "GET Detail User", description = "API for get detail user")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            UserModel.DetailResponse item = service.findDataById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found user", item));
        } catch (Exception e) {
            log.error("Error get data : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "POST Create User", description = "API for create data user")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody UserModel.CreateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            service.saveData(item);
            return ResponseEntity.created(URI.create(urlRoute))
                    .body(new ApiResponse(true, "Successfully created user", null));
        } catch (Exception e) {
            log.error("Error create user : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "PUT Update User", description = "API for update data user")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody UserModel.UpdateRequest item) {
        log.info("PUT " + urlRoute + "/{} endpoint hit", id);
        try {
            service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated user", null));
        } catch (Exception e) {
            log.error("Error update user : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "PUT Update Avatar User", description = "API for update user avatar")
    @PutMapping(value = "{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateAvatar(@PathVariable("id") String id, @RequestPart MultipartFile avatar) {
        log.info("PUT " + urlRoute + "/{}/avatar endpoint hit", id);
        try {
            service.updateAvatar(id, avatar);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated avatar", null));
        } catch (Exception e) {
            log.error("Error update avatar : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "DELETE Data User", description = "API for delete data user")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE " + urlRoute + "/{} endpoint hit", id);
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted user", null));
        } catch (Exception e) {
            log.error("Error delete user : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

}