package com.kopibery.pos.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rl_user_shifts", indexes = {
        @Index(name = "idx_user_shift_secure_id", columnList = "secure_id", unique = true),
        @Index(name = "idx_user_shift_date_user_id", columnList = "date, user_id", unique = true)
})
@Data
public class RlUserShift extends AbstractEntity {

    @Override public Long getId() {return super.getId();}

    @Override public String getSecureId() {return super.getSecureId();}

    @Override public Boolean getIsActive() {return super.getIsActive();}

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "secure_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "shift_id", referencedColumnName = "secure_id")
    private MsShift shift;

    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "secure_id")
    private MsJobPosition position;
}