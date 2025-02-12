package com.kopibery.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppDetailMenuOrderProjection {

    private String id;
    private String name;
    private String image;
    private Integer price;
    private Integer quantity;

}
