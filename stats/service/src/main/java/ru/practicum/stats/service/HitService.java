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
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<StatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        int mode = (uris != null ? 1 : 0) | (unique != null ? 2 : 0);
        var list = new ArrayList<StatsResponse>();
        switch (mode) {
            case 0:
                list.addAll(hitRepository.findAllByTimestampBetweenNotUniqueIp(start, end));
                break;
            case 1:
                list.addAll(hitRepository.findAllByTimestampBetweenNotUniqueIpAndUriIsIn(start, end, uris));
                break;
            case 2:
                list.addAll(hitRepository.findAllByTimestampBetweenUniqueIp(start, end));
                break;
            case 3:
                list.addAll(hitRepository.findAllByTimestampBetweenUniqueIpAndUriIsIn(start, end, uris));
                break;
        }
        return list;
    }

}
