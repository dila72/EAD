package com.example.ead_backend.mapper;

import com.example.ead_backend.dto.NotificationDTO;
import com.example.ead_backend.model.entity.Notification;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Notification entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    /**
     * Convert Notification entity to NotificationDTO.
     *
     * @param entity the notification entity
     * @return the notification DTO
     */
    NotificationDTO toDto(Notification entity);
}
