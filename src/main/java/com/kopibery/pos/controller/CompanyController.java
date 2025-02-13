package com.kopibery.pos.controller;

import com.kopibery.pos.model.CompanyModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.CompanyService;
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

@RestController
@AllArgsConstructor
@RequestMapping(CompanyController.urlRoute)
@Tag(name = "Company API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class CompanyController {

    static final String urlRoute = "/cms/v1/company";

    private CompanyService service;

    @PreAuthorize("hasAuthority('company.view')")
    @Operation(summary = "Get List Company", description = "Get List Company")
    @GetMapping
    public ResponseEntity<?> listIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "isParent", required = false, defaultValue = "false") Boolean isParent) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<CompanyModel.CompanyIndexResponse> response = service.listIndex(pages, limit, sortBy, direction, keyword, isParent);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list company", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company.read')")
    @Operation(summary = "Get detail Company", description = "Get detail Company")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            CompanyModel.CompanyDetailResponse item = service.findDataBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found company", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company.create')")
    @Operation(summary = "Create Company", description = "Create Company")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CompanyModel.CompanyCreateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            CompanyModel.CompanyDetailResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/am/company/"))
                    .body(new ApiResponse(true, "Successfully created company", response));
        } catch (Exception e) {
            log.error("Error create company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company.update')")
    @Operation(summary = "Update Company", description = "Update Company")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody CompanyModel.CompanyUpdateRequest item) {
        log.info("PUT " + urlRoute + "/{id} endpoint hit");
        try {
            CompanyModel.CompanyDetailResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated company", response));
        } catch (Exception e) {
            log.error("Error update company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company.delete')")
    @Operation(summary = "Delete Company", description = "Delete Company")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE " + urlRoute + "/{id} endpoint hit");
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted company", null));
        } catch (Exception e) {
            log.error("Error delete company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
