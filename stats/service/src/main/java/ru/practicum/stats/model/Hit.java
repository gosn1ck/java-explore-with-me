package ru.practicum.stats.model;

import lombok.*;
import ru.practicum.stats.dto.StatsResponse;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedNativeQuery(name = "findAllByTimestampBetweenNotUniqueIp",
        query = "SELECT app AS app, uri AS uri, count(ip) as hits FROM hits AS h " +
                "WHERE hit_time BETWEEN :start AND :end " +
                "GROUP BY app, uri ORDER BY count(ip) desc",
        resultSetMapping = "Mapping.StatsResponse")
@NamedNativeQuery(name = "findAllByTimestampBetweenUniqueIp",
        query = "SELECT app AS app, uri AS uri, count(distinct ip) as hits FROM hits AS h " +
            "WHERE hit_time BETWEEN :start AND :end " +
            "GROUP BY app, uri ORDER BY count(distinct ip) desc",
        resultSetMapping = "Mapping.StatsResponse")
@NamedNativeQuery(name = "findAllByTimestampBetweenNotUniqueIpAndUriIsIn",
        query = "SELECT app AS app, uri AS uri, count(ip) as hits FROM hits AS h " +
                "WHERE hit_time BETWEEN :start AND :end " +
                "AND (h.uri IN :uris) " +
                "GROUP BY app, uri ORDER BY count(ip) desc",
        resultSetMapping = "Mapping.StatsResponse")
@NamedNativeQuery(name = "findAllByTimestampBetweenUniqueIpAndUriIsIn",
        query = "SELECT app AS app, uri AS uri, count(distinct ip) as hits FROM hits AS h " +
                "WHERE hit_time BETWEEN :start AND :end " +
                "AND (h.uri IN :uris) " +
                "GROUP BY app, uri ORDER BY count(distinct ip) desc",
        resultSetMapping = "Mapping.StatsResponse")
@SqlResultSetMapping(name = "Mapping.StatsResponse",
        classes = @ConstructorResult(
                targetClass = StatsResponse.class, columns = {
                    @ColumnResult(name = "app", type = String.class),
                    @ColumnResult(name = "uri", type = String.class),
                    @ColumnResult(name = "hits", type = Integer.class)
                }))
@Table(name = "hits")
public class Hit {
    @Id
    @SequenceGenerator(
            name = "hit_id_sequence",
            sequenceName = "hit_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "hit_id_sequence"
    )
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "hit_time", nullable = false)
    private LocalDateTime timestamp;
}
