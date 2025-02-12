package com.kopibery.pos.controller;

import com.kopibery.pos.model.TransactionModel;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.response.PaginationCmsResponse;
import com.kopibery.pos.response.ResultPageResponseDTO;
import com.kopibery.pos.service.TransactionService;
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
@RequestMapping(TransactionController.urlRoute)
@Tag(name = "Transaction API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class TransactionController {

    static final String urlRoute = "/cms/v1/transaction";

    private final TransactionService service;

    @PreAuthorize("hasAuthority('transaction.view')")
    @Operation(summary = "GET List Transaction", description = "API for get list transaction index with pagination")
    @GetMapping
    public ResponseEntity<?> listDataTransactionIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "desc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");

        try {
            ResultPageResponseDTO<TransactionModel.IndexResponse> response = service.findDataIndex(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list transaction", response));
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('transaction.read')")
    @Operation(summary = "GET Detail Transaction", description = "API for get detail transaction")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            TransactionModel.DetailResponse item = service.findDataById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found transaction", item));
        } catch (Exception e) {
            log.error("Error get data : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('transaction.create')")
    @Operation(summary = "POST Create Transaction", description = "API for create data transaction")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody TransactionModel.CreateUpdateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            TransactionModel.IndexResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create(urlRoute))
                    .body(new ApiResponse(true, "Successfully created transaction", response));
        } catch (Exception e) {
            log.error("Error create transaction : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('transaction.update')")
    @Operation(summary = "PUT Update Transaction", description = "API for update data transaction")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody TransactionModel.CreateUpdateRequest item) {
        log.info("PUT " + urlRoute + "/{} endpoint hit", id);
        try {
            TransactionModel.IndexResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated transaction", response));
        } catch (Exception e) {
            log.error("Error update transaction : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('transaction.delete')")
    @Operation(summary = "DELETE Data Transaction", description = "API for delete data transaction")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE " + urlRoute + "/{} endpoint hit", id);
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted transaction", null));
        } catch (Exception e) {
            log.error("Error delete transaction : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

}
