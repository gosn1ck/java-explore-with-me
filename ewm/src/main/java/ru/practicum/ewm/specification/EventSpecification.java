package ru.practicum.ewm.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;

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

    public static Specification<Event> adminSearchWithDate(
            List<User> users, List<Category> categories, List<EventState> states,
            LocalDateTime start, LocalDateTime end) {
        return Specification
                .where(belongsToInitiator(users))
                .and(belongsToState(states))
                .and(belongsToCategory(categories))
                .and(eventDateAfter(start))
                .and(eventDateBefore(end));
    }

    public static Specification<Event> adminSearchWithoutDate(
            List<User> users, List<Category> categories, List<EventState> states) {
        return Specification
                .where(belongsToInitiator(users))
                .and(belongsToState(states))
                .and(belongsToCategory(categories));
    }

    private static Specification<Event> annotationLike(String annotation) {
        return (root, query, builder) -> {
            if (annotation.isEmpty()) {
                return builder.conjunction();
            }
            return builder.like(builder.upper(root.get("annotation")), "%" + annotation.toUpperCase() + "%");
        };
    }

    private static Specification<Event> descriptionLike(String description) {
        return (root, query, builder) -> {
            if (description.isEmpty()) {
                return builder.conjunction();
            }
            return builder.like(builder.upper(root.get("description")), "%" + description.toUpperCase() + "%");
        };
    }

    private static Specification<Event> belongsToCategory(List<Category> categories) {
        return (root, query, builder) -> {
            if (categories.isEmpty()) {
                return builder.conjunction();
            }
            return builder.in(root.get("category")).value(categories);
        };
    }

    private static Specification<Event> isPaid(Boolean paid) {
        return (root, query, builder) -> {
            if (paid == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("paid"), paid);
        };
    }

    private static Specification<Event> isAvailable(Boolean onlyAvailable) {
        if (onlyAvailable == null) {
            return (root, query, builder) -> builder.conjunction();
        } else if (onlyAvailable) {
            return Specification
                    .where(isParticipantLimitEqualZero().or(isAvailableForRequest()));
        } else {
            return (root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("participantLimit"), 0);
        }
    }

    private static Specification<Event> isParticipantLimitEqualZero() {
        return (root, query, builder) ->
                builder.equal(root.get("participantLimit"), 0);
    }

    private static Specification<Event> isAvailableForRequest() {
        return (root, query, builder) ->
                builder.equal(root.get("availableForRequest"), true);
    }

    private static Specification<Event> isPublished() {
        return (root, query, builder) ->
                builder.equal(root.get("state"), PUBLISHED);
    }

    private static Specification<Event> eventDateAfter(LocalDateTime date) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("eventDate"), date);
    }

    private static Specification<Event> eventDateBefore(LocalDateTime date) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("eventDate"), date);
    }

    private static Specification<Event> belongsToInitiator(List<User> users) {
        return (root, query, builder) -> {
            if (users.isEmpty()) {
                return builder.conjunction();
            }
            return builder.in(root.get("initiator")).value(users);
        };
    }

    private static Specification<Event> belongsToState(List<EventState> states) {
        return (root, query, builder) -> {
            if (states == null) {
                return builder.conjunction();
            }
            return builder.in(root.get("state")).value(states);
        };
    }

}