package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "compilations",
        uniqueConstraints = {
                @UniqueConstraint(name = "compilation_title_unique", columnNames = "title")
        })
public class Compilation {
    @Id
    @SequenceGenerator(
            name = "compilation_id_sequence",
            sequenceName = "compilation_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "compilation_id_sequence"
    )
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;

    @ManyToMany(fetch = LAZY, cascade = ALL)
    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();
}
