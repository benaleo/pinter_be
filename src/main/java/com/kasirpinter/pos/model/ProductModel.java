package com.kasirpinter.pos.model;

import com.kasirpinter.pos.model.AdminModelBaseDTOResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

public class ProductModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ProductIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private Integer price;
        private Integer hppPrice;
        private Boolean isActive;
        private String categoryName;
        private Integer stock;
        private Boolean isUnlimited;
        private Boolean isUpSale;
        private String image;
    }

    @Data
    @AllArgsConstructor
    public static class DetailResponse {
        private String name;
        private Integer price;
        private Integer hppPrice;
        private Boolean isActive;
        private String categoryId;
        private String categoryName;
        private Integer stock;
        private Boolean isUnlimited;
        private Boolean isUpSale;
        private String image;
    }

    @Data
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private Integer price;
        private Integer hppPrice;

        private Integer stock;
        private Boolean isUnlimited;
        private Boolean isUpSale;
        private Boolean isActive;

        private String categoryId;
        private MultipartFile image;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private Integer price;
        private Integer hppPrice;

        private Integer stock;
        private Boolean isUnlimited;
        private Boolean isUpSale;
        private Boolean isActive;

        private String categoryId;
        private MultipartFile image;
    }
}
