package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationResponse;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @GetMapping
    public ResponseEntity<List<CompilationResponse>> getAll(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size) {
        log.info("Get all compilations, from {}, size {}", from, size);
        var compilations = compilationService.getAll(pinned, from, size);
        return ResponseEntity.ok(
                compilations.stream().map(compilationMapper::entityToResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationResponse> get(@PathVariable("compId") Long id) {
        log.info("Get compilation by id: {}", id);
        var response = compilationService.findById(id);
        return response.map(compilationMapper::entityToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
