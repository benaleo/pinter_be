package com.kopibery.pos.model.projection;

import com.kopibery.pos.enums.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductCategoryIndexProjection {

    private String id;
    private String name;
    private Boolean isActive;
    private ProductCategoryType type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
