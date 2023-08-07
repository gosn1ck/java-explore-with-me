package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentResponse;
import ru.practicum.ewm.model.Comment;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface CommentMapper {
    Comment dtoToEntity(CommentDto dto);

    @Mapping(target = "created", dateFormat = DATE_FORMAT)
    CommentResponse entityToResponse(Comment entity);
}