package ru.practicum.stats.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Hit {
    @Id
    @SequenceGenerator(
            name = "hit_id_sequence",
            sequenceName = "hit_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
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
