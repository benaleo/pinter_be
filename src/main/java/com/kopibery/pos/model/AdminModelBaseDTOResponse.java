package com.kopibery.pos.model;

import lombok.Data;

@Data
public class AdminModelBaseDTOResponse {
    private String id;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private String updatedBy;
}