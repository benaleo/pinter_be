package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "am_roles")
@Data
public class Roles extends AbstractEntity implements SecureIdentifiable {

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "am_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permissions> permissions;

    @OneToMany(mappedBy = "role",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> listPermissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = listPermissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission().getName())).collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(name));
        return authorities;
    }

}
