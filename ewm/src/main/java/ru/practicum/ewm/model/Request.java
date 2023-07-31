package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @SequenceGenerator(
            name = "request_id_sequence",
            sequenceName = "request_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "request_id_sequence"
    )
    @Column(name = "id", updatable = false)
    private Long id;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "requester_id", referencedColumnName = "id", nullable = false)
    private User requester;
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime created = LocalDateTime.now();;

}
