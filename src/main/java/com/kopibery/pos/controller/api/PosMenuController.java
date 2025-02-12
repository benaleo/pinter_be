package com.kopibery.pos.controller.api;

import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.PostMenuService;
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
@RequestMapping(PosMenuController.urlRoute)
@Tag(name = "Menu API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class PosMenuController {

    static final String urlRoute = "/api/v1/pos";

    private final PostMenuService service;

    // index menu
    @Operation(summary = "Get List Menu", description = "Get List Menu")
    @GetMapping("/menu")
    public ResponseEntity<?> listMenuIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "1000") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category
    ) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<MenuModel.MenuIndexResponse> response = service.listMenuIndex(pages, limit, sortBy, direction, keyword, category);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list menu", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Error get list menu", null));
        }
    }

    // index menu
    @Operation(summary = "Get List Menu", description = "Get List Menu")
    @GetMapping("/transaction")
    public ResponseEntity<?> listOrderIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "1000") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "paymentMethod", required = false) TransactionType paymentMethod,
            @RequestParam(name = "paymentStatus", required = false) TransactionStatus paymentStatus
    ) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<MenuModel.OrderIndexResponse> response = service.listOrderIndex(pages, limit, sortBy, direction, keyword, paymentMethod, paymentStatus);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list order transaction", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Error get list order transaction", null));
        }
    }

}
