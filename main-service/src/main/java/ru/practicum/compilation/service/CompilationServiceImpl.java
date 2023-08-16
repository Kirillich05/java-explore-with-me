package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repo;
    private final EventService eventService;
    private final EntityManager entityManager;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = eventService.findAllByIds(newCompilationDto.getEvents());
        Compilation compilation = repo.save(convertToModel(newCompilationDto, events));

        return convertToDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest) {
        var compilation = findOrThrow(compId);

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.findAllByIds(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        var updatedCompilation = repo.save(compilation);
        return convertToDto(updatedCompilation);
    }

    @Override
    @Transactional
    public void delete(long compId) {
        findOrThrow(compId);
        repo.deleteById(compId);
    }

    @Override
    public CompilationDto getById(long compId) {
        var compilation = findOrThrow(compId);
        List<EventShortDto> events = eventService.getEventShortWithViewsAndRequests(compilation.getEvents());
        return convertToDto(compilation, events);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compilation> query = criteriaBuilder.createQuery(Compilation.class);
        Root<Compilation> root = query.from(Compilation.class);
        Predicate criteria = criteriaBuilder.conjunction();
        Predicate isPinned;

        if (pinned != null) {
            if (!pinned) {
                isPinned = criteriaBuilder.isFalse(root.get("pinned"));
            } else {
                isPinned = criteriaBuilder.isTrue(root.get("pinned"));
            }
            criteria = criteriaBuilder.and(criteria, isPinned);
        }

        query.select(root).where(criteria);
        List<Compilation> compilations = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        return compilations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Compilation findOrThrow(long id) {
        return repo.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Compilation by id " + id + " was not found")
                );
    }

    private CompilationDto convertToDto(Compilation compilation, List<EventShortDto> events) {
        var compilationDto = mapper.map(compilation, CompilationDto.class);
        compilationDto.setEvents(events);
        return compilationDto;
    }

    private CompilationDto convertToDto(Compilation compilation) {
        return mapper.map(compilation, CompilationDto.class);
    }

    private Compilation convertToModel(NewCompilationDto compilationDto, List<Event> events) {
        var compilation = mapper.map(compilationDto, Compilation.class);
        compilation.setEvents(events);
        return compilation;
    }
}
