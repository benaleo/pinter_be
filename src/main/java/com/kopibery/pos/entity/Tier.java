package com.kopibery.pos.entity;

import com.kopibery.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tier", indexes = {
        @Index(name = "idx_tier_secure_id", columnList = "secure_id", unique = true)
})
public class Tier extends AbstractEntity implements SecureIdentifiable {

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

    private String name;

    private String icon;

    private String point;

}
