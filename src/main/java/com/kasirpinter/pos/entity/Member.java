package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.AbstractEntity;
import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
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
@Table(name = "member", indexes = {
        @Index(name = "idx_member_secure_id", columnList = "secure_id", unique = true)
})
public class Member extends AbstractEntity implements SecureIdentifiable {
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

    private String email;

    private String phone;

    private String tier;

}
