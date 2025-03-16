package com.kasirpinter.pos.controller;

import com.kasirpinter.pos.model.JobPositionModel;
import com.kasirpinter.pos.response.ApiResponse;
import com.kasirpinter.pos.response.PaginationCmsResponse;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.MsJobPositionService;
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
@RequestMapping(MsJobPositionController.urlRoute)
@Tag(name = "[Masterdata] Position API")
@Slf4j
@SecurityRequirement(name = "Authorization")
public class MsJobPositionController {

    static final String urlRoute = "/cms/v1/job-position";

    private MsJobPositionService service;

    @PreAuthorize("hasAuthority('job_positions.view')")
    @Operation(summary = "Get List Job Position", description = "Get List Job Position")
    @GetMapping
    public ResponseEntity<?> listJobPositionIndex(
            @RequestParam(name = "pages", required = false, defaultValue = "0") Integer pages,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "keyword", required = false) String keyword
    ){
        // response true
        log.info("GET " + urlRoute + " endpoint hit");
        try {
            ResultPageResponseDTO<JobPositionModel.JobPositionIndexResponse> response = service.listIndex(pages, limit, sortBy, direction, keyword);
            return ResponseEntity.ok().body(new PaginationCmsResponse<>(true, "Success get list company", response));
        } catch (Exception e) {
            log.error("Error get index : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('job_positions.read')")
    @Operation(summary = "Get detail Job Position", description = "Get detail Job Position")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        log.info("GET " + urlRoute + "/{id} endpoint hit");
        try {
            JobPositionModel.JobPositionDetailResponse item = service.findJobPositionBySecureId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully found company", item));
        } catch (Exception e) {
            log.error("Error get detail : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('job_positions.create')")
    @Operation(summary = "Create Job Position", description = "Create Job Position")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody JobPositionModel.JobPositionCreateRequest item) {
        log.info("POST " + urlRoute + " endpoint hit");
        try {
            JobPositionModel.JobPositionDetailResponse response = service.saveData(item);
            return ResponseEntity.created(URI.create("/cms/v1/am/company/"))
                    .body(new ApiResponse(true, "Successfully created company", response));
        } catch (Exception e) {
            log.error("Error create company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('job_positions.update')")
    @Operation(summary = "Update Job Position", description = "Update Job Position")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable("id") String id, @Valid @RequestBody JobPositionModel.JobPositionUpdateRequest item) {
        log.info("PUT " + urlRoute + "/{id} endpoint hit");
        try {
            JobPositionModel.JobPositionDetailResponse response = service.updateData(id, item);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated company", response));
        } catch (Exception e) {
            log.error("Error update company : {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAuthority('job_positions.delete')")
    @Operation(summary = "Delete Job Position", description = "Delete Job Position")
    @DeleteMapping("/{id}")
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
