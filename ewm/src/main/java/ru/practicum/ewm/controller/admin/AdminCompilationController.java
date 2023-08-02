package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompilationResponse> add(@RequestBody @Valid NewCompilationDto dto) {
        log.info("New compilation registration {}", dto);
        var savedCompilation = compilationService.add(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCompilation.getId()).toUri();
        return ResponseEntity.created(location)
                .body(compilationMapper.entityToResponse(savedCompilation));
    }

    @PatchMapping(consumes = "application/json", path = "/{compId}")
    public ResponseEntity<CompilationResponse> update(
            @RequestBody @Valid UpdateCompilationRequest dto,
            @PathVariable("compId") Long id) {
        log.info("Update category {} with id {}", dto, id);
        var response = compilationService.update(dto, id);
        return response.map(compilationMapper::entityToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long id) {
        log.info("Remove compilation with id: {}", id);
        compilationService.deleteById(id);
    }

}
