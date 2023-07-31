package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    @Column(name = "lat")
    private Double lat;
    @Column(name = "lon")
    private Double lon;
}
