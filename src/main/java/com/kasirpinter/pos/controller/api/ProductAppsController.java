package com.kasirpinter.pos.controller.api;

import com.kasirpinter.pos.controller.ProductCategoryController;
import com.kasirpinter.pos.controller.ProductController;
import com.kasirpinter.pos.model.ProductCategoryModel;
import com.kasirpinter.pos.model.ProductModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.ProductCategoryService;
import com.kasirpinter.pos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(ProductAppsController.urlRoute)
@Tag(name = "Product API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class ProductAppsController {

    static final String urlRoute = "/api/v1/ms/";

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    @Operation(summary = "Get List Product", description = "Get List Product")
    @GetMapping("/product")
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
            ResultPageResponseDTO<ProductModel.ProductIndexResponse> response = productService.listIndexApp(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list product", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "Get List Product Category", description = "Get List Product Category")
    @GetMapping("/product-category")
    public ResponseEntity<?> listIndexProductCategory(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        // response true
        log.info("GET " + urlRoute + "/product-category endpoint hit");
        try {
            ResultPageResponseDTO<ProductCategoryModel.IndexResponse> response = productCategoryService.listIndexInApp(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list product_category", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }


}
