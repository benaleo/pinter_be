package com.kasirpinter.pos.controller;

import com.kasirpinter.pos.model.ProductModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping(ProductController.urlRoute)
@Tag(name = "Product API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class ProductController {

    static final String urlRoute = "/cms/v1/product";

    private final ProductService service;

    @PreAuthorize("hasAuthority('product.view')")
    @Operation(summary = "Get List Product", description = "Get List Product")
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
            ResultPageResponseDTO<ProductModel.ProductIndexResponse> response = service.listIndex(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list product", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product.read')")
    @Operation(summary = "Get detail Product", description = "Get detail Product")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            ProductModel.DetailResponse item = service.findDataBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found product", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product.create')")
    @Operation(summary = "Create Product", description = "Create Product")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> create(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "0" ) Integer price,
            @RequestParam(required = false, defaultValue = "0" ) Integer hppPrice,
            @RequestParam(required = false, defaultValue = "0" ) Integer stock,
            @RequestParam(required = false, defaultValue = "false") Boolean isUnlimited,
            @RequestParam(required = false, defaultValue = "false") Boolean isUpSale,
            @RequestParam(required = false, defaultValue = "false") Boolean isActive,
            @RequestParam String categoryId

    ) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            ProductModel.CreateRequest request = new ProductModel.CreateRequest(name, price, hppPrice, stock, isUnlimited, isUpSale, isActive, categoryId, image);
            ProductModel.ProductIndexResponse response = service.saveData(request);
            return ResponseEntity.created(URI.create("/cms/v1/product/"))
                    .body(new ApiResponse(true, "Successfully created product", response));
        } catch (Exception e) {
            log.error("Error create product : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product.update')")
    @Operation(summary = "Update Product", description = "Update Product")
    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> update(
            @PathVariable("id") String id,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "0" ) Integer price,
            @RequestParam(required = false, defaultValue = "0" ) Integer hppPrice,
            @RequestParam(required = false, defaultValue = "0" ) Integer stock,
            @RequestParam(required = false, defaultValue = "false") Boolean isUnlimited,
            @RequestParam(required = false, defaultValue = "false") Boolean isUpSale,
            @RequestParam(required = false, defaultValue = "false") Boolean isActive,
            @RequestParam String categoryId
    ) {
        log.info("PUT " + urlRoute + "/{id} endpoint hit");
        try {
            ProductModel.UpdateRequest request = new ProductModel.UpdateRequest(name, price, hppPrice, stock, isUnlimited, isUpSale, isActive, categoryId, image);
            ProductModel.ProductIndexResponse response = service.updateData(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated product", response));
        } catch (Exception e) {
            log.error("Error update product : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('product.delete')")
    @Operation(summary = "Delete Product", description = "Delete Product")
    @PutMapping("{id}/delete")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE " + urlRoute + "/{id} endpoint hit");
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted product", null));
        } catch (Exception e) {
            log.error("Error delete product : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
