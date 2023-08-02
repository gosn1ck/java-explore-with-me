package ru.practicum.ewm.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.model.EventState.PUBLISHED;

public class EventSpecification {

    public static Specification<Event> publicSearchWithoutRange(
            String text, List<Category> categories, Boolean paid, Boolean onlyAvailable) {

        LocalDateTime currentTime = LocalDateTime.now();
        return Specification
                .where(annotationLike(text).or(descriptionLike(text)))
                .and(belongsToCategory(categories))
                .and(isPaid(paid))
                .and(eventDateAfter(currentTime))
                .and(isAvailable(onlyAvailable))
                .and(isPublished());
    }

    public static Specification<Event> publicSearchWithRange(
            String text, List<Category> categories, Boolean paid,
            Boolean onlyAvailable, LocalDateTime start, LocalDateTime end) {

        return Specification
                .where(annotationLike(text).or(descriptionLike(text)))
                .and(belongsToCategory(categories))
                .and(isPaid(paid))
                .and(isAvailable(onlyAvailable))
                .and(isPublished())
                .and(eventDateAfter(start))
                .and(eventDateBefore(end));
    }

    public static Specification<Event> annotationLike(String annotation) {
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("annotation")), "%" + annotation.toUpperCase() + "%");
    }

    public static Specification<Event> descriptionLike(String description) {
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("description")), "%" + description.toUpperCase() + "%");
    }

    public static Specification<Event> belongsToCategory(List<Category> categories) {
        return (root, query, builder) ->
                builder.in(root.get("category")).value(categories);
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, builder) ->
                builder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> isAvailable(Boolean onlyAvailable) {
        if (onlyAvailable) {
            return Specification
                    .where(isParticipantLimitEqualZero().or(isAvailableForRequest()));
        } else {
            return (root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("participantLimit"), 0);
        }
    }

    public static Specification<Event> isParticipantLimitEqualZero() {
        return (root, query, builder) ->
                builder.equal(root.get("participantLimit"), 0);
    }

    public static Specification<Event> isAvailableForRequest() {
        return (root, query, builder) ->
                builder.equal(root.get("availableForRequest"), true);
    }

    public static Specification<Event> isPublished() {
        return (root, query, builder) ->
                builder.equal(root.get("state"), PUBLISHED);
    }

    public static Specification<Event> eventDateAfter(LocalDateTime date) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("eventDate"), date);
    }

    public static Specification<Event> eventDateBefore(LocalDateTime date) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("eventDate"), date);
    }

}
