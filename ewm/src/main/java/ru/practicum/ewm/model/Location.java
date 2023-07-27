package ru.practicum.ewm.model;

import javax.persistence.Embeddable;

@Embeddable
public class Location {
    private Double lat;
    private Double lon;
}
