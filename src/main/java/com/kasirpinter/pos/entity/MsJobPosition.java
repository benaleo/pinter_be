package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.AbstractEntity;
import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ms_job_positions", indexes = {
        @Index(name = "idx_job_position_secure_id", columnList = "secure_id", unique = true)
})
@Data
public class MsJobPosition extends AbstractEntity implements SecureIdentifiable {

    @Override public Long getId() {return super.getId();}

    @Override public String getSecureId() {return super.getSecureId();}

    @Override public Boolean getIsActive() {return super.getIsActive();}

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_ids", columnDefinition = "text")
    private String categoryIds;

    @Column(name = "description", columnDefinition = "text")
    private String description;

}
