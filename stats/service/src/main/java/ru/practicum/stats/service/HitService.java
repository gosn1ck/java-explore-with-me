package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsResponse;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Transactional
    public Hit add(HitDto dto) {
        var hit = hitMapper.dtoToEntity(dto);
        return hitRepository.save(hit);
    }

    public List<StatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        var list = new ArrayList<Hit>();
        if (uris == null) {
            list.addAll(hitRepository.findAllByTimestampBetween(start, end));
        } else {
            list.addAll(hitRepository.findAllByTimestampBetweenAndUriIsIn(start, end, uris));
        }
        return mapToStats(list, unique);
    }

    private List<StatsResponse> mapToStats(ArrayList<Hit> hits, Boolean unique) {
        var statMap = new HashMap<String, StatsResponse>();
        var uniqueIps = new HashMap<StatsResponse, List<String>>();

        for (Hit hit : hits) {
            var stat = statMap.getOrDefault(hit.getUri(), new StatsResponse(hit.getApp(), hit.getUri(), 0));
            if (unique == TRUE) {
                var ips = uniqueIps.getOrDefault(stat, new ArrayList<>());
                if (!ips.contains(hit.getIp())) {
                    ips.add(hit.getIp());
                    stat.setHits(stat.getHits() + 1);
                }
                uniqueIps.put(stat, ips);
            } else {
                stat.setHits(stat.getHits() + 1);
            }
            statMap.put(hit.getUri(), stat);
        }
        return new ArrayList<>(statMap.values());
    }
}
