package com.kasirpinter.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class JobPositionIndexProjection {

    private String id;
    private String name;
    private String description;
    private String companyId;
    private String companyName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
