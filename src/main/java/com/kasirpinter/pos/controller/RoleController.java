package com.kasirpinter.pos.controller;


import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kasirpinter.pos.model.RoleModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(RoleController.urlRoute)
@Tag(name = "Role API")
@SecurityRequirement(name = "Authorization")
public class RoleController {

    static final String urlRoute = "/cms/v1/am/role";

    private RoleService service;

    @PreAuthorize("hasAuthority('role.view')")
    @Operation(summary = "Get List Role", description = "Get List Role")
    @GetMapping
    public ResponseEntity<?> listFollowUser(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // response true
        try {
            ResultPageResponseDTO<RoleModel.RoleIndexResponse> response = service.listData(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list role", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('role.read')")
    @Operation(summary = "Get detail Role", description = "Get detail Role")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET /cms/v1/am/role/{id} endpoint hit");
        try {
            RoleModel.RoleDetailResponse item = service.findDataBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found role", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('role.create')")
    @Operation(summary = "Create Role", description = "Create Role")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody RoleModel.RoleCreateUpdateRequest item) {
        log.info("POST /cms/v1/am/role endpoint hit");
        try {
            RoleModel.RoleDetailResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/am/role/"))
                    .body(new ApiResponse(true, "Successfully created role", response));
        } catch (Exception e) {
            log.error("Error create role : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('role.update')")
    @Operation(summary = "Update Role", description = "Update Role")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody RoleModel.RoleCreateUpdateRequest item) {
        log.info("PUT /cms/v1/am/role/{id} endpoint hit");
        try {
            RoleModel.RoleDetailResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated role", response));
        } catch (Exception e) {
            log.error("Error update role : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('role.delete')")
    @Operation(summary = "Delete Role", description = "Delete Role")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE /cms/v1/am/role/{id} endpoint hit");
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted role", null));
        } catch (Exception e) {
            log.error("Error delete role : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

