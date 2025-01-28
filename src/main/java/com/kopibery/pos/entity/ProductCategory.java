package com.kopibery.pos.entity;

import com.kopibery.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_category", indexes = {
        @Index(name = "idx_product_category_secure_id", columnList = "secure_id", unique = true)
})
public class ProductCategory extends AbstractEntity implements SecureIdentifiable {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> listProduct;

    public int getTotalProduct() {
        return listProduct.size();
    }

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
