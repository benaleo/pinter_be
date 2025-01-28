package com.kopibery.pos.entity;

import com.kopibery.pos.entity.impl.SecureIdentifiable;
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
@Table(name = "product", indexes = {
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

    @Column(name = "is_upsale")
    private Boolean isUpSale = false;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "is_unlimited")
    private Boolean isUnlimited = false;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "secure_id")
    private ProductCategory category;

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
