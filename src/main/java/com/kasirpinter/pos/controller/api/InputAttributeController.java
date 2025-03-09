package com.kasirpinter.pos.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.service.InputAttributeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping(InputAttributeController.urlRoute)
@Tag(name = "Input Attribute API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class InputAttributeController {

    static final String urlRoute = "/api/v1/input";

    private final InputAttributeService inputAttributeService;

    @GetMapping("/product-category")
    public ResponseEntity<?> getProductCategory() {
        log.info("GET " + urlRoute + "/product-category endpoint hit");
        try {
            List<Map<String, String>> response = inputAttributeService.getListProductCategory();
            return ResponseEntity.ok().body(new ApiResponse(true, "Success get product category", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get product category", null));
        }
    }

    @GetMapping("/company")
    public ResponseEntity<?> getCompany() {
        log.info("GET " + urlRoute + "/company endpoint hit");
        try {
            List<Map<String, String>> response = inputAttributeService.getListCompany();
            return ResponseEntity.ok().body(new ApiResponse(true, "Success get company", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get company", null));
        }
    }

}
