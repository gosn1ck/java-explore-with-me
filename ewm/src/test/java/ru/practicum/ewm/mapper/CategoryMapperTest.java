package ru.practicum.ewm.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.model.Category;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CategoryMapperImpl.class})
class CategoryMapperTest {

    @Autowired
    private CategoryMapper underTest;

    private static final String NAME = "Концерты";

    @DisplayName("Запрос категории мэпится в категорию для записи в БД")
    @Test
    void shouldMapCategoryDtoToCategory() {
        val dto = getDto();
        val entity = underTest.dtoToEntity(dto);

        assertEquals(dto.getName(), entity.getName());
    }

    @DisplayName("Категория мэпится в категорию для ответа контроллера")
    @Test
    void shouldMapCategoryToCategoryResponse() {
        val entity = getEntity();
        val response = underTest.entityToResponse(entity);

        assertEquals(response.getId(), entity.getId());
        assertEquals(response.getName(), entity.getName());
    }

    @DisplayName("Категория обновляется ")
    @Test
    void shouldUpdateCategory() {
        val entity = getEntity();
        val dto = getDto();
        dto.setName("Очень громкая музыка");
        underTest.updateEntity(entity, dto);

        assertEquals(entity.getName(), dto.getName());
    }

    private static CategoryDto getDto() {
        return CategoryDto.builder()
                .name(NAME)
                .build();
    }

    private static Category getEntity() {
        return Category.builder()
                .id(1L)
                .name(NAME)
                .build();
    }

}