package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.AbstractEntity;
import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ms_shifts", indexes = {
        @Index(name = "idx_shift_secure_id", columnList = "secure_id", unique = true)
})
@Data
public class MsShift extends AbstractEntity implements SecureIdentifiable {

    @Override public Long getId() {return super.getId();}

    @Override public String getSecureId() {return super.getSecureId();}

    @Override public Boolean getIsActive() {return super.getIsActive();}

    private String name;

    @Column(name = "start_time", columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(name = "end_time", columnDefinition = "TIME")
    private LocalTime endTime;

    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "secure_id")
    private Company company;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "shift",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RlUserShift> userShifts;

    public List<String> getUserShiftSecureIds(){
        return userShifts.stream().map(RlUserShift::getUser).map(Users::getSecureId).toList();
    }
}