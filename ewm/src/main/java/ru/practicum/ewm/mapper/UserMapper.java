package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserResponse;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User dtoToEntity(UserDto dto);

    void updateEntity(@MappingTarget User entity, UserDto dto);

    UserResponse entityToUserResponse(User user);

}