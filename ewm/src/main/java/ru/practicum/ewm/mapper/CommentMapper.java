package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentResponse;
import ru.practicum.ewm.dto.CommentShortResponse;
import ru.practicum.ewm.model.Comment;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface CommentMapper {

    Comment dtoToEntity(CommentDto dto);

    @Mapping(target = "created", dateFormat = DATE_FORMAT)
    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "event", source = "event.id")
    CommentResponse entityToResponse(Comment entity);

    @Mapping(target = "created", dateFormat = DATE_FORMAT)
    CommentShortResponse entityToShortResponse(Comment entity);

    void updateEntity(@MappingTarget Comment entity, CommentDto dto);

}