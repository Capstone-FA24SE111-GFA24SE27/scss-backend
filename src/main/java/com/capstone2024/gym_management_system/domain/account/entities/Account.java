package com.capstone2024.gym_management_system.domain.account.entities;

import com.capstone2024.gym_management_system.domain.common.entity.BaseEntity;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.domain.account.enums.Status;
import com.capstone2024.gym_management_system.infrastructure.converters.RoleConverter;
import com.capstone2024.gym_management_system.infrastructure.converters.StatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account extends BaseEntity implements UserDetails {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "account")
    private Profile profile;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Convert(converter = StatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.asSecurityRole()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return getEmail();
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
        return (getStatus() == Status.ACTIVE);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
