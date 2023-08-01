package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsMapper;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepo repo;

    @Transactional
    @Override
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        var endpointHit = StatsMapper.toEndpointHit(endpointHitDto);
        repo.save(endpointHit);
        return StatsMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        List<ViewStats> stats;

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                stats = repo.getUniqueStatsWithoutUris(start, end);
                return stats.stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            } else {
                stats = repo.getStatsWithoutUris(start, end);
                return stats.stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            }
        } else if (unique) {
            stats = repo.getUniqueStats(start, end, uris);
            return stats.stream()
                    .map(StatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        } else {
            stats = repo.getStats(start, end, uris);
            return stats.stream()
                    .map(StatsMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        }
    }
}
