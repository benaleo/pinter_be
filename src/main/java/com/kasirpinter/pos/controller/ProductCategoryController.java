package com.kasirpinter.pos.controller;

import com.kasirpinter.pos.model.ProductCategoryModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.kasirpinter.pos.exception.BadRequestException;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping(ProductCategoryController.urlRoute)
@Tag(name = "Product Category API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class ProductCategoryController {

    static final String urlRoute = "/cms/v1/product_category";

    private ProductCategoryService service;

    @PreAuthorize("hasAuthority('product_categories.view')")
    @Operation(summary = "Get List Product Category", description = "Get List Product Category")
    @GetMapping
    public ResponseEntity<?> listIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<ProductCategoryModel.ProductCategoryIndexResponse> response = service.listIndex(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list product_category", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product_categories.read')")
    @Operation(summary = "Get detail Product Category", description = "Get detail Product Category")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            ProductCategoryModel.ProductCategoryDetailResponse item = service.findDataBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found product_category", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product_categories.create')")
    @Operation(summary = "Create Product Category", description = "Create Product Category")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ProductCategoryModel.ProductCategoryCreateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            ProductCategoryModel.ProductCategoryIndexResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/product_category/"))
                    .body(new ApiResponse(true, "Successfully created product_category", response));
        } catch (BadRequestException e) {
            log.error("Error create product_category : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error create product_category : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product_categories.update')")
    @Operation(summary = "Update Product Category", description = "Update Product Category")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody ProductCategoryModel.ProductCategoryUpdateRequest item) {
        log.info("PUT " + urlRoute + "/{id} endpoint hit");
        try {
            ProductCategoryModel.ProductCategoryIndexResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated product_category", response));
        } catch (Exception e) {
            log.error("Error update product_category : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product_categories.update')")
    @Operation(summary = "Update Product Category", description = "Update Product Category")
    @PutMapping("{id}/delete")
    public ResponseEntity<ApiResponse> updateSoftDelete(@PathVariable("id") String id) {
        log.info("PUT " + urlRoute + "/{}/delete endpoint hit", id);
        try {
            service.updateSoftDelete(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted product_category", null));
        } catch (Exception e) {
            log.error("Error delete product_category : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product_categories.delete')")
    @Operation(summary = "Delete Product Category", description = "Delete Product Category")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE " + urlRoute + "/{id} endpoint hit");
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted product_category", null));
        } catch (Exception e) {
            log.error("Error delete product_category : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
