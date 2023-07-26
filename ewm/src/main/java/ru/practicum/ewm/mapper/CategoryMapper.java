package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.CategoryResponse;
import ru.practicum.ewm.model.Category;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category dtoToEntity(CategoryDto dto);

    CategoryResponse entityToResponse(Category entity);

    void updateEntity(@MappingTarget Category entity, CategoryDto dto);

}
