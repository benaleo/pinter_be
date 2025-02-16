package com.kopibery.pos.controller.api;

import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import com.kopibery.pos.model.MenuModel;
import com.kopibery.pos.model.TransactionModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.PosService;
import com.kopibery.pos.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(PosController.urlRoute)
@Tag(name = "Menu API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class PosController {

    static final String urlRoute = "/api/v1/pos";

    private final PosService posService;
    private final TransactionService transactionService;

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
            ResultPageResponseDTO<MenuModel.MenuIndexResponse> response = posService.listMenuIndex(pages, limit, sortBy, direction, keyword, category);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list menu", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get list menu", null));
        }
    }

    // category menu
    @Operation(summary = "Get List Menu", description = "Get List Menu")
    @GetMapping("/menu/category")
    public ResponseEntity<?> listMenuCategoryIndex() {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            List<Map<String, String>> response = posService.listMenuCategoryIndex();
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list menu", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get list menu", null));
        }
    }

    // make transaction order
    @Operation(summary = "POST Create Transaction", description = "API for create data transaction")
    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody TransactionModel.CreateUpdateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            TransactionModel.IndexResponse response = transactionService.saveData(item);
            return ResponseEntity.created(URI.create(urlRoute))
                    .body(new ApiResponse(true, "Successfully created transaction", response));
        } catch (Exception e) {
            log.error("Error create transaction : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }


    // update transaction order
    @Operation(summary = "PUT Update Transaction", description = "API for updating data transaction")
    @PutMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody TransactionModel.CreateUpdateRequest item, @PathVariable String transactionId) {
        log.info("PUT " + urlRoute + " endpoint hit");
        try {
            TransactionModel.IndexResponse response = transactionService.updateData(transactionId, item);
            return ResponseEntity.ok().body(new ApiResponse(true, "Successfully updated transaction", response));
        } catch (Exception e) {
            log.error("Error update transaction : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // update transaction order to cancelled
    @Operation(summary = "PUT Update Transaction", description = "API for updating data transaction")
    @PutMapping("/transaction/{transactionId}/cancel")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable String transactionId) {
        log.info("PUT " + urlRoute + " endpoint hit");
        try {
            transactionService.updateStatusToCancel(transactionId);
            return ResponseEntity.ok().body(new ApiResponse(true, "Successfully cancelled the transaction", null));
        } catch (Exception e) {
            log.error("Error update transaction : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
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
            ResultPageResponseDTO<MenuModel.OrderIndexResponse> response = posService.listOrderIndex(pages, limit, sortBy, direction, keyword, paymentMethod, paymentStatus);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list order transaction", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Error get list order transaction", null));
        }
    }

}
