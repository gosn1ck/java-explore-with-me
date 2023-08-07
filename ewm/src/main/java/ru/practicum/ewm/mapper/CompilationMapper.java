package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.ewm.dto.CompilationResponse;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.service.EventService;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE, uses = {EventService.class})
public interface CompilationMapper {

    @Mapping(target = "pinned", source = "pinned", defaultValue = "false")
    Compilation dtoToEntity(NewCompilationDto dto);

    void updateEntity(@MappingTarget Compilation entity, UpdateCompilationRequest dto);

    CompilationResponse entityToResponse(Compilation entity);

}