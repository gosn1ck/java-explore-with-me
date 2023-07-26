package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public Category add(CategoryDto dto) {
        var category = categoryMapper.dtoToEntity(dto);

        try {
            return categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("category with email %s already exists", dto.getName());
        }
    }

    @Transactional
    public Optional<Category> update(CategoryDto dto, Long id) {
        var category = getCategory(id);
        categoryMapper.updateEntity(category, dto);
        try {
            return Optional.of(categoryRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("category with email %s already exists", dto.getName());
        }
    }

    public List<Category> getAll(Integer from, Integer size) {
        var page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).toList();
    }

    public Optional<Category> findById(Long id) {
        return Optional.of(getCategory(id));
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("category with id %d not found", id));
    }

}
