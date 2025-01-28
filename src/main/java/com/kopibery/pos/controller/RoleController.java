package com.kopibery.pos.controller;


import com.kopibery.pos.model.RoleModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
        ResultPageResponseDTO<RoleModel.IndexResponse> response = service.listData(pages, limit, sortBy, direction, keyword);
        return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list role", response));
    }

    @PreAuthorize("hasAuthority('role.read')")
    @Operation(summary = "Get detail Role", description = "Get detail Role")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET /cms/v1/am/role/{id} endpoint hit");
        try {
            RoleModel.DetailResponse item = service.findDataBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found role", item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('role.create')")
    @Operation(summary = "Create Role", description = "Create Role")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody RoleModel.CreateUpdateRequest item) {
        log.info("POST /cms/v1/am/role endpoint hit");
        try {
            RoleModel.DetailResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/am/role/"))
                    .body(new ApiResponse(true, "Successfully created role", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('role.update')")
    @Operation(summary = "Update Role", description = "Update Role")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody RoleModel.CreateUpdateRequest item) {
        log.info("PUT /cms/v1/am/role/{id} endpoint hit");
        try {
            RoleModel.DetailResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated role", response));
        } catch (Exception e) {
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
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}

