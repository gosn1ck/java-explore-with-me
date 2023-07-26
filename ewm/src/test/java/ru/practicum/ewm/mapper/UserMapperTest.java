package ru.practicum.ewm.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UserMapperImpl.class})
class UserMapperTest {

    private static final String NAME = "Ivan";
    private static final String EMAIL = "ivan@yandex.ru";

    @Autowired
    private UserMapper underTest;

    @DisplayName("Запрос пользователь мэпится в пользователя для записи в БД")
    @Test
    void shouldMapUserDtoToUser() {
        val userDto = new UserDto(NAME, EMAIL);
        val user = underTest.dtoToEntity(userDto);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @DisplayName("Пользователь мэпится в пользователя для ответа контроллеру")
    @Test
    void shouldMapUserToUserRequest() {
        val user = getUser();
        val request = underTest.entityToUserResponse(user);

        assertEquals(request.getId(), user.getId());
        assertEquals(request.getName(), user.getName());
        assertEquals(request.getEmail(), user.getEmail());
    }

    @DisplayName("Поля пользователя обновляются по запросу пользователя")
    @Test
    void shouldUpdateEntity() {
        val userDtoWithOnlyName = new UserDto();
        String updatedName = "Update";
        userDtoWithOnlyName.setName(updatedName);

        var user = getUser();
        String emailBeforeUpdate = user.getEmail();
        underTest.updateEntity(user, userDtoWithOnlyName);

        assertEquals(updatedName, user.getName());
        assertEquals(emailBeforeUpdate, user.getEmail());

        val userDtoWithOnlyEmail = new UserDto();
        String updatedEmail = "update@yandex.ru";
        userDtoWithOnlyEmail.setEmail(updatedEmail);

        user = getUser();
        String nameBeforeUpdate = user.getName();
        underTest.updateEntity(user, userDtoWithOnlyEmail);

        assertEquals(nameBeforeUpdate, user.getName());
        assertEquals(updatedEmail, user.getEmail());

    }

    private User getUser() {
        val user = new User();
        user.setId(1L);
        user.setName(NAME);
        user.setEmail(EMAIL);
        return user;
    }

}