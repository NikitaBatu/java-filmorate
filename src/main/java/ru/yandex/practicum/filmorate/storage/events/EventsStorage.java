package ru.yandex.practicum.filmorate.storage.events;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventsStorage {
    List<Event> getUserEvents(long userId);

    void addEvent(long userId, long entityId, String eventType, String operation);
}
