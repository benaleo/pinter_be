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
@Table(name = "company_categories", indexes = {
        @Index(name = "idx_company_categories_secure_id", columnList = "secure_id", unique = true)
})
public class CompanyCategory extends AbstractEntity implements SecureIdentifiable {

    private String name;

    private String category;

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
