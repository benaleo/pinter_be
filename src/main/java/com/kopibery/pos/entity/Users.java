package com.kopibery.pos.entity;

import com.kopibery.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
public class Users extends AbstractEntity implements UserDetails, SecureIdentifiable {

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

    @Column(name = "avatar", columnDefinition = "bytea")
    private byte[] avatar;

    @Column(name = "avatar_name")
    private String avatarName;

    @Column(name = "name")
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @EqualsAndHashCode.Exclude
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "secure_id")
    @EqualsAndHashCode.Exclude
    private Company company;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RlUserShift> userShift;

    // parse attendance

    public String userClockIn(){
        return userShift.stream()
                .filter(e -> e.getDate().equals(LocalDate.now()))
                .findFirst()
                .filter(e -> e.getTsIn() != null)
                .map(e -> e.getTsIn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .orElse(null);
    }

    public String userClockOut(){
        return userShift.stream()
                .filter(e -> e.getDate().equals(LocalDate.now()))
                .findFirst()
                .filter(e -> e.getTsOut() != null)
                .map(e -> e.getTsOut().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .orElse(null);
    }

    
    public String getNow(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }


    // Getters and setters

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getIsActive();
    }

    // Add getters and setters for id, email, and enabled
}