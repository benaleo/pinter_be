package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.AbstractEntity;
import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.Product;
import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
import com.kasirpinter.pos.enums.ProductCategoryType;
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

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "secure_id", updatable = false)
    private Company company;

    @OneToMany(mappedBy = "category",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> listProduct;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProductCategoryType type = ProductCategoryType.MENU;

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
