package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserResponse;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        log.info("Get all users, from {}, size {}, ids {}", from, size, ids);
        var users = userService.getAll(from, size, ids);
        return ResponseEntity.ok(
                users.stream().map(userMapper::entityToUserResponse).collect(Collectors.toList()));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> add(@Valid @RequestBody UserDto dto) {
        log.info("New user registration {}", dto);
        var savedUser = userService.add(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location)
                .body(userMapper.entityToUserResponse(savedUser));
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") Long id) {
        log.info("Remove user with id: {}", id);
        userService.deleteById(id);
    }

}
