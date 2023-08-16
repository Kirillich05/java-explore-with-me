package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest);

    void delete(long compId);

    CompilationDto getById(long compId);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);
}
