package com.kasirpinter.pos.controller;

import com.kasirpinter.pos.model.CompanyCategoryModel;
import com.kasirpinter.pos.model.CompanyCategoryModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.CompanyCategoryService;
import com.kasirpinter.pos.service.CompanyService;
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
@Tag(name = "Company Category API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class CompanyCategoryController {

    static final String urlRoute = "/cms/v1/company-category";

    private CompanyCategoryService service;

    @PreAuthorize("hasAuthority('company_category.view')")
    @Operation(summary = "Get List Company Category", description = "Get List Company Category")
    @GetMapping
    public ResponseEntity<?> listCompanyCategoryIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword
    ){
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<CompanyCategoryModel.CompanyCategoryIndexResponse> response = service.listIndex(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list company", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company_category.read')")
    @Operation(summary = "Get detail Company Category", description = "Get detail Company Category")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            CompanyCategoryModel.CompanyCategoryDetailResponse item = service.findCompanyCategoryBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found company", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company_category.create')")
    @Operation(summary = "Create Company Category", description = "Create Company Category")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CompanyCategoryModel.CompanyCategoryCreateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            CompanyCategoryModel.CompanyCategoryDetailResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/am/company/"))
                    .body(new ApiResponse(true, "Successfully created company", response));
        } catch (Exception e) {
            log.error("Error create company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company_category.update')")
    @Operation(summary = "Update Company Category", description = "Update Company Category")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody CompanyCategoryModel.CompanyCategoryUpdateRequest item) {
        log.info("PUT " + urlRoute + "/{id} endpoint hit");
        try {
            CompanyCategoryModel.CompanyCategoryDetailResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated company", response));
        } catch (Exception e) {
            log.error("Error update company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('company_category.delete')")
    @Operation(summary = "Delete Company Category", description = "Delete Company Category")
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
