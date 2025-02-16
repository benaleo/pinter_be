package com.kopibery.pos.controller.api;

import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(InputAttributeController.urlRoute)
@Tag(name = "Input Attribute API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class InputAttributeController {

    static final String urlRoute = "/api/v1/input";

    private final ProductCategoryService productCategoryService;

    @GetMapping("/product-category")
    public ResponseEntity<?> getProductCategory() {
        log.info("GET " + urlRoute + "/product-category endpoint hit");
        try{
            List<Map<String, String>> response = productCategoryService.getListInputForm();
            return ResponseEntity.ok().body(new ApiResponse(true, "Success get product category", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get product category", null));
        }
    }
}
