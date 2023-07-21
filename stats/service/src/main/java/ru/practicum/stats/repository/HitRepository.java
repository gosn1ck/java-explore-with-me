package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    List<Hit> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Hit> findAllByTimestampBetweenAndUriIsIn(LocalDateTime start, LocalDateTime end, List<String> uris);

//    List<Hit> findDistinctByIpAndTimestampBetween(LocalDateTime start, LocalDateTime end);

}
