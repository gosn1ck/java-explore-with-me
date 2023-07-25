package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitResponse;
import ru.practicum.stats.model.Hit;

import static ru.practicum.stats.util.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HitMapper {

    @Mapping(target = "timestamp", dateFormat = DATE_FORMAT)
    Hit dtoToEntity(HitDto dto);

    @Mapping(source = "timestamp", target = "timestamp", dateFormat = DATE_FORMAT)
    HitResponse entityToResponse(Hit entity);

}
