package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.repository.CompilationRepository;

import java.util.List;
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
        getCompilation(id);
        compilationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Compilation> getAll(Boolean pinned, Integer from, Integer size) {
        var page = PageRequest.of(from / size, size, Sort.by("id"));
        if (pinned == null) {
            return compilationRepository.findAll(page).toList();
        } else {
            return compilationRepository.findAllByPinned(pinned, page);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Compilation> findById(Long id) {
        return Optional.of(getCompilation(id));
    }

    private Compilation getCompilation(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("compilation with id %d not found", id));
    }

}
