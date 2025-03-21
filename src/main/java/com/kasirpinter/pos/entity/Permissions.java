package com.kasirpinter.pos.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "am_permissions")
@Data
public class Permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
