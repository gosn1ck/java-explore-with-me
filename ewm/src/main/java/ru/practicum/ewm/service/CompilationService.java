package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.repository.CompilationRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Transactional
    public Compilation add(NewCompilationDto dto) {
        var compilation = compilationMapper.dtoToEntity(dto);

        try {
            return compilationRepository.saveAndFlush(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("compilation with title %s already exists", dto.getTitle());
        }
    }

    @Transactional
    public Optional<Compilation> update(UpdateCompilationRequest dto, Long id) {
        var category = getCompilation(id);
        compilationMapper.updateEntity(category, dto);
        try {
            return Optional.of(compilationRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("compilation with title %s already exists", dto.getTitle());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        compilationRepository.deleteById(id);
    }

    private Compilation getCompilation(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("compilation with id %d not found", id));
    }

}
