package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment_likes")
public class CommentLike {

    @Id
    @SequenceGenerator(
            name = "comment_like_id_sequence",
            sequenceName = "comment_like_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "comment_like_id_sequence"
    )
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "comment_id", referencedColumnName = "id", nullable = false)
    private Comment comment;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LikeType type;

}