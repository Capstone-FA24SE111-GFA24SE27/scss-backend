package com.capstone2024.gym_management_system.infrastructure.converters;

import com.capstone2024.gym_management_system.domain.account.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        return dbData != null ? Role.valueOf(dbData) : null;
    }
}
