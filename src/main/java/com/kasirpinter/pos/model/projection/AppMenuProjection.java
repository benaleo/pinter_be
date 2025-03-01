package com.kasirpinter.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppMenuProjection {

    private String productId;
    private String name;
    private String image;
    private Integer price;
    private String categoryId;
    private String categoryName;
    private Integer stock;

}
