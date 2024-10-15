package com.capstone2024.scss.domain.account.entities;

import com.capstone2024.scss.domain.account.enums.LoginMethod;
import com.capstone2024.scss.domain.common.entity.BaseEntity;
import com.capstone2024.scss.domain.account.enums.Role;
import com.capstone2024.scss.domain.account.enums.Status;
import com.capstone2024.scss.infrastructure.converters.RoleConverter;
import com.capstone2024.scss.infrastructure.converters.StatusConverter;
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

    @OneToOne(mappedBy = "account")
    private Profile profile;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;

    @Convert(converter = StatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @SuppressWarnings("java:S1948")
    @OneToMany(mappedBy = "account")
    private List<LoginType> loginTypes;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.asSecurityRole()));
    }

    @Override
    public String getPassword() {
        List<LoginType> loginTypes = getLoginTypes();
        if(loginTypes != null) {
            for (LoginType loginType : loginTypes) {
                if (loginType.getMethod().name().equals(LoginMethod.DEFAULT.name())) {
                    return loginType.getPassword();
                }
            }
        }
        return null;
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
