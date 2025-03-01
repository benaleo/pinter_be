package com.kasirpinter.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProductIndexProjection {
    private String id;
    private String name;
    private Integer price;
    private Integer hppPrice;

    private Integer stock;
    private Boolean isUnlimited;
    private Boolean isUpSale;
    private Boolean isActive;

    private String categoryName;

    private byte[] image;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
