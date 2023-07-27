package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Event;

import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    Event dtoToEntity(NewEventDto dto);

    void updateEntity(@MappingTarget Event entity, UpdateEventUserRequest dto);

    EventFullDto entityToEventFullDto(Event event);

    EventShortDto entityToEventShortDto(Event event);

}
