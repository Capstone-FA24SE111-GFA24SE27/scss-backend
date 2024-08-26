package com.capstone2024.gym_management_system.infrastructure.configuration.security;

import com.capstone2024.gym_management_system.domain.account.entities.Account;
import com.capstone2024.gym_management_system.domain.account.enums.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class CustomSecurityUtils {

    public Set<Role> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Collections.emptySet();
        }
        return authentication.getAuthorities().stream()
                .map(ga -> StringUtils.substring(ga.getAuthority(), "ROLE_".length()))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }
        Object accountObj = authentication.getPrincipal();
        if (!(accountObj instanceof Account)) {
            return null;
        }
        return (Account) accountObj;
    }
}
