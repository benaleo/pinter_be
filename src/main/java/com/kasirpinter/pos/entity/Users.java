package com.kasirpinter.pos.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import com.kasirpinter.pos.entity.AbstractEntity;
import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.entity.Tier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kasirpinter.pos.entity.impl.SecureIdentifiable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(unique = true)
    private String phone;

    @Column(name = "address")
    private String address;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    @EqualsAndHashCode.Exclude
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "secure_id")
    @EqualsAndHashCode.Exclude
    private Company company;

    @ManyToOne
    @JoinColumn(name = "tier_id", referencedColumnName = "secure_id")
    private Tier tier;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RlUserShift> userShift;

    // parse attendance

    public String userClockIn() {
        return userShift.stream()
                .filter(e -> e.getDate().equals(LocalDate.now()))
                .findFirst()
                .filter(e -> e.getTsIn() != null)
                .map(e -> e.getTsIn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .orElse(null);
    }

    public String userClockOut() {
        return userShift.stream()
                .filter(e -> e.getDate().equals(LocalDate.now()))
                .findFirst()
                .filter(e -> e.getTsOut() != null)
                .map(e -> e.getTsOut().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .orElse(null);
    }

    public String getNow() {
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