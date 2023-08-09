package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CommentLikeDto;
import ru.practicum.ewm.dto.CommentLikeResponse;
import ru.practicum.ewm.model.CommentLike;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface CommentLikeMapper {

    @Mapping(target = "comment", ignore = true)
    CommentLike dtoToEntity(CommentLikeDto dto);

    @Mapping(target = "created", dateFormat = DATE_FORMAT)
    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "comment", source = "comment.id")
    CommentLikeResponse entityToResponse(CommentLike entity);

}