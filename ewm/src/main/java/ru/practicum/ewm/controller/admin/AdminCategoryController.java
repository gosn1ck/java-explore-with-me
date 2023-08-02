package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.CategoryResponse;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoriesService;
    private final CategoryMapper categoryMapper;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryResponse> add(@RequestBody @Valid CategoryDto dto) {
        log.info("New category registration {}", dto);
        var savedCategory = categoriesService.add(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategory.getId()).toUri();
        return ResponseEntity.created(location)
                .body(categoryMapper.entityToResponse(savedCategory));
    }

    @PatchMapping(consumes = "application/json", path = "/{catId}")
    public ResponseEntity<CategoryResponse> update(@RequestBody @Valid CategoryDto dto, @PathVariable("catId") Long id) {
        log.info("Update category {} with id {}", dto, id);
        var response = categoriesService.update(dto, id);
        return response.map(categoryMapper::entityToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Remove category with id: {}", id);
        categoriesService.deleteById(id);
    }

}
