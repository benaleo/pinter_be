package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ms_products", indexes = {
        @Index(name = "idx_product_secure_id", columnList = "secure_id", unique = true)
})
public class Product extends AbstractEntity implements SecureIdentifiable {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price")
    private Integer price = 0;

    @Column(name = "hpp_price")
    private Integer hppPrice = 0;

    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    @Column(name = "image_name", columnDefinition = "text")
    private String imageName;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @Column(name = "is_upsale")
    private Boolean isUpSale = false;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "is_unlimited")
    private Boolean isUnlimited = false;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "secure_id", nullable = false)
    private ProductCategory category;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public String getSecureId() {
        return super.getSecureId();
    }

    @Override
    public Boolean getIsActive() {
        return super.getIsActive();
    }
}
