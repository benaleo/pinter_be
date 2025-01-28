package com.kopibery.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CompanyIndexProjection {

    private String id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private String parentId;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

}
