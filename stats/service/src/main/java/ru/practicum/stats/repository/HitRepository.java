package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.StatsResponse;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(name = "findAllByTimestampBetweenNotUniqueIp", nativeQuery = true)
    List<StatsResponse> findAllByTimestampBetweenNotUniqueIp(
        @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(name = "findAllByTimestampBetweenUniqueIp", nativeQuery = true)
    List<StatsResponse> findAllByTimestampBetweenUniqueIp(
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(name = "findAllByTimestampBetweenNotUniqueIpAndUriIsIn", nativeQuery = true)
    List<StatsResponse> findAllByTimestampBetweenNotUniqueIpAndUriIsIn(
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query(name = "findAllByTimestampBetweenUniqueIpAndUriIsIn", nativeQuery = true)
    List<StatsResponse> findAllByTimestampBetweenUniqueIpAndUriIsIn(
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

}
