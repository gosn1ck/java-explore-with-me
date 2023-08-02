package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User add(UserDto dto) {
        var user = userMapper.dtoToEntity(dto);
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("user with email %s already exists", dto.getEmail());
        }

    }

    @Transactional(readOnly = true)
    public List<User> getAll(Integer from, Integer size, List<Long> ids) {
        var page = PageRequest.of(from / size, size, Sort.by("id"));
        if (ids == null) {
            return userRepository.findAll(page).toList();
        } else {
            return userRepository.findAllByIdIn(ids, page);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        getUser(id);
        userRepository.deleteById(id);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
    }

}
