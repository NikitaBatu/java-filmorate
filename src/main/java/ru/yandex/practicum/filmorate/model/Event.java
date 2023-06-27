package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class Event {
    private long eventId;
    private long timestamp;
    @NotNull
    private long userId;
    private String eventType;
    private String operation;
    @NotNull
    private long entityId;
}
