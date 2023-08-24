package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsClient extends BaseClient {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatisticsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> addHit(@RequestBody EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end) {
        return getStatistics(start, end, null, null);
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStatistics(start, end, uris, null);
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, Boolean unique) {
        return getStatistics(start, end, null, unique);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Time is wrong");
        }

        StringBuilder uri = new StringBuilder("/stats?start={start}&end={end}");
        Map<String, Object> parameters = Map.of("start", start.format(formatter), "end", end.format(formatter));

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                uri.append("&uris=").append(u);
            }
        }

        if (unique != null) {
            uri.append("&unique=").append(unique);
        }

        return get(uri.toString(), parameters);
    }
}
