package com.kasirpinter.pos.controller;

import com.kasirpinter.pos.model.UserShiftModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.UserShiftService;
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
@RequestMapping(UserShiftController.urlRoute)
@Tag(name = "User Shift API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class UserShiftController {

    static final String urlRoute = "/cms/v1/am/user/shift";

    private UserShiftService service;

    @PreAuthorize("hasAuthority('shift.view')")
    @Operation(summary = "Get List Shift", description = "Get List Shift")
    @GetMapping
    public ResponseEntity<?> listIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<UserShiftModel.ShiftIndexResponse> response = service.listIndex(pages, limit, sortBy,
                    direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list shift", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('shift.read')")
    @Operation(summary = "Get detail Shift", description = "Get detail Shift")
    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            UserShiftModel.ShiftDetailResponse item = service.findDataBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found shift", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('shift.create')")
    @Operation(summary = "Create Shift", description = "Create Shift")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody UserShiftModel.ShiftCreateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            UserShiftModel.ShiftDetailResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/am/shift/"))
                    .body(new ApiResponse(true, "Successfully created shift", response));
        } catch (Exception e) {
            log.error("Error create shift : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('shift.update')")
    @Operation(summary = "Update Shift", description = "Update Shift")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id,
            @Valid @RequestBody UserShiftModel.ShiftUpdateRequest item) {
        log.info("PUT " + urlRoute + "/{id} endpoint hit");
        try {
            UserShiftModel.ShiftDetailResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated shift", response));
        } catch (Exception e) {
            log.error("Error update shift : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('shift.delete')")
    @Operation(summary = "Delete Shift", description = "Delete Shift")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") String id) {
        log.info("DELETE " + urlRoute + "/{id} endpoint hit");
        try {
            service.deleteData(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted shift", null));
        } catch (Exception e) {
            log.error("Error delete shift : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // list shift person
    @PreAuthorize("hasAuthority('shift.view')")
    @Operation(summary = "Get List Shift", description = "Get List Shift")
    @GetMapping("{id}/assigned")
    public ResponseEntity<?> listIndexAssigned(
            @PathVariable("id") String shiftId,
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // response true
        log.info("GET " + urlRoute + "/{}/assigned endpoint hit", shiftId);
        try {
            ResultPageResponseDTO<UserShiftModel.ShiftAssignedResponse> response = service.listIndexAssigned(pages, limit,
                    sortBy,
                    direction, keyword, shiftId);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list shift assigned", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('shift.create')")
    @Operation(summary = "Create Shift", description = "Create Shift")
    @PostMapping("{id}/assigned")
    public ResponseEntity<ApiResponse> createAssigned(
        @PathVariable("id") String shiftId,
        @Valid @RequestBody UserShiftModel.ShiftAssignedRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            service.saveDataAssigned(item);
            return ResponseEntity.created(URI.create(urlRoute + "/" + shiftId + "/assigned"))
                    .body(new ApiResponse(true, "Successfully created user in shift", null));
        } catch (Exception e) {
            log.error("Error create assigned user in shift : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('shift.delete')")
    @Operation(summary = "Delete Shift", description = "Delete Shift")
    @DeleteMapping("{id}/assigned")
    public ResponseEntity<ApiResponse> deleteAssigned(
        @PathVariable("id") String shiftId, 
        @RequestParam String userId) {
        log.info("DELETE " + urlRoute + "/{id}/assigned?userId={} endpoint hit", userId);
        try {
            service.deleteDataAssigned(shiftId, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted user in shift", null));
        } catch (Exception e) {
            log.error("Error delete user in shift : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

}
