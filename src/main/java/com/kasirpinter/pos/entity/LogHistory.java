package com.kasirpinter.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "log_history")
public class LogHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "model")
    private String model;

    @Column(name = "from_log")
    private String fromLog;

    @Column(name = "to_log")
    private String toLog;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "action_by")
    private String actionBy;

    @Column(name = "ts", updatable = false)
    private LocalDateTime ts;

    @PrePersist
    protected void onCreate() {
        ts = LocalDateTime.now();
    }

}
