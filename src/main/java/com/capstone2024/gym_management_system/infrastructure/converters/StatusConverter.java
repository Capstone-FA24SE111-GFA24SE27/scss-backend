package com.capstone2024.gym_management_system.infrastructure.converters;

import com.capstone2024.gym_management_system.domain.account.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return dbData != null ? Status.valueOf(dbData) : null;
    }
}
