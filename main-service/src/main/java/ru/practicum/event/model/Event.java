package ru.practicum.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.EventState;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    @NotBlank
    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    Category category;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;

    @Column
    Boolean paid;

    @Column(name = "participant_limit")
    long participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    EventState state;

    @Column
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id && participantLimit == event.participantLimit &&
                Objects.equals(annotation, event.annotation) && Objects.equals(category, event.category) &&
                Objects.equals(createdOn, event.createdOn) &&
                Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate) &&
                Objects.equals(initiator, event.initiator) && Objects.equals(location, event.location) &&
                Objects.equals(paid, event.paid) && Objects.equals(publishedOn, event.publishedOn) &&
                Objects.equals(requestModeration, event.requestModeration) &&
                state == event.state && Objects.equals(title, event.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, category, createdOn, description, eventDate, initiator, location, paid,
                participantLimit, publishedOn, requestModeration, state, title);
    }
}
