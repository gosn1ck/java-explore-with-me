package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.CategoryService;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE, uses = {CategoryService.class})
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    @Mapping(target = "paid", source = "paid", defaultValue = "false")
    @Mapping(target = "requestModeration", source = "requestModeration", defaultValue = "true")
    Event dtoToEntity(NewEventDto dto);

    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    void updateEntity(@MappingTarget Event entity, UpdateEventUserRequest dto);

    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    void updateEntity(@MappingTarget Event entity, UpdateEventAdminRequest dto);

    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "createdOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "publishedOn", dateFormat = DATE_FORMAT)
    EventFullDto entityToEventFullDto(Event event);

    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    EventShortDto entityToEventShortDto(Event event);

}