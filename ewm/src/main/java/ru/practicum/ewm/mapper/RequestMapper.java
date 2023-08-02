package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.RequestResponse;
import ru.practicum.ewm.model.Request;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestResponse entityToResponse(Request entity);
}
