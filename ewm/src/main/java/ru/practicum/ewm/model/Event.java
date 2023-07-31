package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

//@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @SequenceGenerator(
            name = "event_id_sequence",
            sequenceName = "event_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "event_id_sequence"
    )
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "location", nullable = false)
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participantLimit")
    private Integer participantLimit;

    @Column(name = "requestModeration")
    private Boolean requestModeration;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User initiator;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;

    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations;

}
