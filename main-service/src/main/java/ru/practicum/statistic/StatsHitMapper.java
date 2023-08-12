package ru.practicum.statistic;

import ru.practicum.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class StatsHitMapper {

    public static EndpointHitDto toEndpointHit(HttpServletRequest request,
                                               String app) {
        return EndpointHitDto.builder()
                .id(null)
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
