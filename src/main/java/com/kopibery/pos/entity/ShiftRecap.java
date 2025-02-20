package com.kopibery.pos.entity;

import com.kopibery.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "user_shift_recap", indexes = {
        @Index(name = "idx_user_shift_recap_secure_id", columnList = "secure_id", unique = true)
})
@Data
public class ShiftRecap extends AbstractEntity implements SecureIdentifiable {


    @Override public Long getId() {return super.getId();}

    @Override public String getSecureId() {return super.getSecureId();}

    @Override public Boolean getIsActive() {return super.getIsActive();}

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", referencedColumnName = "secure_id")
    private MsShift shift;

    @Column(name = "cash")
    private Integer cash;

    @Column(name = "total_income")
    private Integer totalIncome;

    @Column(name = "total_expense")
    private Integer totalExpense;

    @Column(name = "total_profit")
    private Integer totalProfit;

}
